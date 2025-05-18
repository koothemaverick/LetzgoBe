package com.letzgo.LetzgoBe.domain.chat.chatMessage.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.ChatMessageDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessage;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessagePage;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessageRead;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.MessageContent;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.event.ChatEventPublisher;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.repository.ChatMessageReadRepository;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.repository.ChatMessageRepository;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.repository.MessageContentRepository;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoom;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomMember;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.repository.ChatRoomMemberRepository;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.repository.ChatRoomRepository;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import com.letzgo.LetzgoBe.global.s3.S3Service;
import com.letzgo.LetzgoBe.global.webSocket.payload.ChatWebSocketPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.letzgo.LetzgoBe.global.webSocket.payload.ChatWebSocketPayload.MessageType.MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MessageContentRepository messageContentRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageReadRepository chatMessageReadRepository;
    private final S3Service s3Service;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatEventPublisher chatEventPublisher;

    // 메시지 읽음 처리
    @Override
    @Transactional
    public void readChatMessage(Long messageId, Long memberId) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATMESSAGE_NOT_FOUND));
        if (chatMessage.getChatMessageReads() == null) {
            chatMessage.setChatMessageReads(new ArrayList<>()); // Initialize the list if it's null
        }
        // DB에서 읽은 기록이 있는지 확인
        Optional<ChatMessageRead> existingRead = chatMessageReadRepository.findByChatMessageIdAndMemberId(messageId, memberId);
        if (!existingRead.isPresent()) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));
            ChatMessageRead readRecord = ChatMessageRead.builder()
                    .chatMessage(chatMessage)
                    .member(member)
                    .readAt(LocalDateTime.now())
                    .build();
            chatMessageReadRepository.save(readRecord);
        }
    }

    // 해당 채팅방의 이전 메시지 가져오기
    @Override
    @Transactional
    public Page<ChatMessageDto> findByChatRoomId(Long chatRoomId, Pageable pageable, LoginUserDto loginUser) {
        try {
            checkPageSize(pageable.getPageSize());
            ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberIdAndChatRoomId(loginUser.getId(), chatRoomId);
            if (chatRoomMember == null) {
                log.warn("chatRoomMember not found - chatRoomId: {}, loginUserId: {}", chatRoomId, loginUser.getId());
                throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
            }
            readAllChatMessages(loginUser.getId(), chatRoomId, chatRoomMember);
            Pageable sortedPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
            Page<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(chatRoomId, sortedPageable);
            List<String> stringMessageIds = chatMessages.stream()
                    .map(chatMessage -> String.valueOf(chatMessage.getId()))
                    .collect(Collectors.toList());
            List<MessageContent> contents = Optional.ofNullable(messageContentRepository.findByIdIn(stringMessageIds))
                    .orElse(Collections.emptyList());
            Map<Long, String> messageContentMap = new HashMap<>();
            for (MessageContent message : contents) {
                try {
                    messageContentMap.put(Long.parseLong(message.getId()), message.getContent());
                } catch (NumberFormatException e) {
                    log.warn("Invalid messageContent ID format: {}", message.getId());
                }
            }
            return chatMessages.map(chatMessage -> {
                String content = messageContentMap.getOrDefault(chatMessage.getId(), "");
                return convertToChatMessageDto(chatMessage, content);
            });
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in findByChatRoomId - chatRoomId: {}, loginUserId: {}, error: {}", chatRoomId, loginUser.getId(), e.getMessage(), e);
            throw new ServiceException(ReturnCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 해당 채팅방에서 메시지 검색(내용)
    @Override
    @Transactional
    public Page<ChatMessageDto> searchByKeyword(Long chatRoomId, String keyword, Pageable pageable, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));
        checkPageSize(pageable.getPageSize());

        // 채팅방 참여멤버만 메시지 조회 가능
        boolean memberExists = chatRoom.getChatRoomMembers().stream()
                .anyMatch(joinedMember -> joinedMember.getMember().getId().equals(loginUser.getId()));
        if (!memberExists) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }

        // 채팅방 내 모든 메시지 ID 조회
        Page<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(chatRoomId, pageable);
        List<String> stringMessageIds = chatMessages.stream()
                .map(chatMessage -> String.valueOf(chatMessage.getId()))
                .collect(Collectors.toList());

        // MongoDB에서 해당 ID들의 메시지 내용 조회
        Map<Long, String> messageContentMap = messageContentRepository.findByIdIn(stringMessageIds).stream()
                .collect(Collectors.toMap(message -> Long.parseLong(message.getId()), MessageContent::getContent));

        // 키워드 포함 여부 검사 후 필터링
        List<ChatMessage> filteredMessages = chatMessages.stream()
                .filter(chatMessage -> {
                    String content = messageContentMap.getOrDefault(chatMessage.getId(), "");
                    return content.contains(keyword); // 키워드 포함 여부 확인
                })
                .collect(Collectors.toList());

        // 필터링된 메시지를 DTO로 변환
        List<ChatMessageDto> chatMessageDtos = filteredMessages.stream()
                .map(chatMessage -> {
                    String content = messageContentMap.getOrDefault(chatMessage.getId(), "");
                    return convertToChatMessageDto(chatMessage, content);
                })
                .collect(Collectors.toList());
        return new PageImpl<>(chatMessageDtos, pageable, chatMessageDtos.size());
    }

    // 해당 채팅방에서 메시지 생성
    @Override
    @Transactional
    public ChatMessageDto writeChatMessage(Long chatRoomId, String content, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));

        // 채팅방 참여 멤버만 메시지 생성 가능
        boolean memberExists = chatRoom.getChatRoomMembers().stream()
                .anyMatch(joinedMember -> joinedMember.getMember().getId().equals(member.getId()));
        if (!memberExists) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }

        // 메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .member(member)
                .chatRoom(chatRoom)
                .build();
        chatMessageRepository.save(chatMessage);

        // MongoDB에 메시지 본문 저장
        MessageContent messageContent = MessageContent.builder()
                .id(String.valueOf(chatMessage.getId()))
                .content(content)
                .build();
        messageContentRepository.save(messageContent);

        // 메시지 보낸 사람은 자동으로 "읽음" 처리
        ChatMessageRead readRecord = ChatMessageRead.builder()
                .chatMessage(chatMessage)
                .member(member)
                .readAt(LocalDateTime.now())
                .build();
        chatMessageReadRepository.save(readRecord);

        // 해당 채팅방의 마지막 메시지 갱신 이벤트 발행
        ChatWebSocketPayload payload = ChatWebSocketPayload.builder()
                .messageType(MESSAGE)
                .memberId(memberId)
                .chatRoomId(chatRoomId)
                .content(content)
                .build();
        chatEventPublisher.publishLastMessageEvent(payload);
        return(convertToChatMessageDto(chatMessage, content));
    }

    // 해당 채팅방에서 이미지 메시지 생성
    @Override
    @Transactional
    public void writeImageMessage(Long chatRoomId, List<MultipartFile> imageFiles, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));

        // 채팅방 참여 멤버만 메시지 생성 가능
        boolean memberExists = chatRoom.getChatRoomMembers().stream()
                .anyMatch(joinedMember -> joinedMember.getMember().getId().equals(loginUser.getId()));
        if (!memberExists) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }

        // 이미지 파일 검증
        if (imageFiles == null || imageFiles.isEmpty() || imageFiles.size() > 5) {
            throw new ServiceException(ReturnCode.FILE_UPLOAD_ERROR);
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile imageFile : imageFiles) {
            if (!imageFile.isEmpty()) {
                try {
                    String imageUrl = s3Service.uploadFile(imageFile, "chat-images");
                    imageUrls.add(imageUrl);
                } catch (IOException e) {
                    throw new ServiceException(ReturnCode.INTERNAL_ERROR);
                }
            }
        }

        // 채팅 메시지 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .member(loginUser.ConvertToMember())
                .chatRoom(chatRoom)
                .imageUrls(imageUrls)
                .build();
        chatMessageRepository.save(chatMessage);

        // 보낸 사람은 바로 읽음 처리
        Member sender = loginUser.ConvertToMember();
        ChatMessageRead readRecord = ChatMessageRead.builder()
                .chatMessage(chatMessage)
                .member(sender)
                .readAt(LocalDateTime.now())
                .build();
        chatMessageReadRepository.save(readRecord);

        // 메시지 생성 이벤트 발행
        ChatMessageDto chatMessageDto = convertToChatMessageDto(chatMessage, null);
        ChatWebSocketPayload imagePayload = ChatWebSocketPayload.builder()
                .messageType(MESSAGE)
                .chatRoomId(chatRoomId)
                .chatMessageDto(chatMessageDto)
                .build();
        chatEventPublisher.publishImageMessageEvent(imagePayload);

        // 해당 채팅방의 마지막 메시지 갱신 이벤트 발행
        ChatWebSocketPayload payload = ChatWebSocketPayload.builder()
                .messageType(MESSAGE)
                .memberId(loginUser.getId())
                .chatRoomId(chatRoomId)
                .content(null)
                .build();
        chatEventPublisher.publishLastMessageEvent(payload);
    }

    // 해당 메시지 삭제
    @Override
    @Transactional
    public void deleteChatMessage(Long messageId, LoginUserDto loginUser) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId).orElseThrow(() -> new ServiceException(ReturnCode.CHATMESSAGE_NOT_FOUND));
        if (!chatMessage.getMember().getId().equals(loginUser.getId())) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }
        if (chatMessage.getImageUrls() != null && !chatMessage.getImageUrls().isEmpty()) {
            s3Service.deleteAllFile(chatMessage.getImageUrls());
        }
        messageContentRepository.deleteById(String.valueOf(messageId));
        chatMessageRepository.delete(chatMessage);
    }

    // 해당 채팅방의 모든 메시지 삭제
    @Override
    @Transactional
    public void deleteAllChatMessages(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom(chatRoom);

        messages.forEach(message -> {
            if (message.getImageUrls() != null && !message.getImageUrls().isEmpty()) {
                s3Service.deleteAllFile(message.getImageUrls());
            }
            messageContentRepository.deleteById(String.valueOf(message.getId()));
        });
        chatMessageRepository.deleteAll(messages);
    }

    // 해당 멤버가 작성한 모든 메시지 삭제
    @Override
    @Transactional
    public void deleteMembersAllChatMessages(Long memberId){
        List<ChatMessage> chatMessages = chatMessageRepository.findByMemberId(memberId);
        for (ChatMessage chatMessage : chatMessages) {
            if (chatMessage.getImageUrls() != null && !chatMessage.getImageUrls().isEmpty()) {
                s3Service.deleteAllFile(chatMessage.getImageUrls());
            }
            if (messageContentRepository.existsById(String.valueOf(chatMessage.getId()))) {
                messageContentRepository.deleteById(String.valueOf(chatMessage.getId()));
            }
            chatMessageRepository.delete(chatMessage);
        }
    }

    // 해당 채팅방 내의 모든 메시지 읽음 처리
    @Transactional
    public void readAllChatMessages(Long memberId, Long chatRoomId, ChatRoomMember chatRoomMember) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));

        List<Long> messageIds = chatMessageRepository.findAllMessageIdsByChatRoomId(chatRoomId);
        if (messageIds.isEmpty()) return;
        Set<Long> alreadyReadSet = new HashSet<>(chatMessageReadRepository.findMessageIdsByMemberIdAndChatRoomId(memberId, chatRoomId));
        List<Long> unreadMessageIds = messageIds.stream()
                .filter(id -> !alreadyReadSet.contains(id))
                .collect(Collectors.toList());
        if (!unreadMessageIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            List<ChatMessageRead> readRecords = unreadMessageIds.stream()
                    .map(messageId -> ChatMessageRead.builder()
                            .chatMessage(ChatMessage.builder().id(messageId).build())
                            .member(member)
                            .readAt(now)
                            .build())
                    .collect(Collectors.toList());
            chatMessageReadRepository.saveAll(readRecords);

            // 상태 업데이트
            chatRoomMember.setLastReadMessageId(Collections.max(messageIds));
            chatRoomMemberRepository.save(chatRoomMember);

            // 채팅방 접속 시 안읽은 메시지들 읽음 이벤트 발행
            ChatWebSocketPayload payload = ChatWebSocketPayload.builder()
                    .messageType(ChatWebSocketPayload.MessageType.READALL)
                    .chatRoomId(chatRoomId)
                    .readMessageIdList(unreadMessageIds)
                    .build();
            chatEventPublisher.publishReadAllMessageEvent(payload);
        }
    }

    // 요청 페이지 수 제한
    private void checkPageSize(int pageSize) {
        int maxPageSize = ChatMessagePage.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new ServiceException(ReturnCode.PAGE_REQUEST_FAIL);
        }
    }

    // ChatMessage를 ChatMessageDto로 변환
    private ChatMessageDto convertToChatMessageDto(ChatMessage chatMessage, String content) {
        Long readMemberCount = chatMessageReadRepository.countByChatMessageId(chatMessage.getId());
        int totalMemberCount = chatMessage.getChatRoom().getChatRoomMembers().size();
        Long unreadCount = (long) totalMemberCount - readMemberCount;
        return ChatMessageDto.builder()
                .id(chatMessage.getId())
                .memberId(chatMessage.getMember().getId())
                .nickname(chatMessage.getMember().getNickname())
                .profileImageUrl(chatMessage.getMember().getProfileImageUrl())
                .content(content)
                .imageUrls(chatMessage.getImageUrls())
                .unreadCount(unreadCount)
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
