package com.letzgo.LetzgoBe.domain.chat.chatMessage.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.req.ChatMessageForm;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.res.ChatMessageDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessage;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessagePage;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessageRead;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.MessageContent;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.eventListener.ChatMessageCreatedEvent;
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
import com.letzgo.LetzgoBe.global.webSocket.ChatWebSocketHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    // 메시지 읽음 처리
    @Override
    @Transactional
    public void readChatMessage(Long messageId, Long memberId) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATMESSAGE_NOT_FOUND));
        boolean alreadyRead = chatMessage.getChatMessageReads().stream()
                .anyMatch(read -> read.getMember().getId().equals(memberId));
        if (!alreadyRead) {
            ChatMessageRead readRecord = ChatMessageRead.builder()
                    .chatMessage(chatMessage)
                    .member(memberRepository.findById(memberId)
                            .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND)))
                    .readAt(LocalDateTime.now())
                    .build();
            chatMessageReadRepository.save(readRecord);
        }
    }

    // 해당 채팅방의 이전 메시지 가져오기
    @Override
    @Transactional
    public Page<ChatMessageDto> findByChatRoomId(Long chatRoomId, Pageable pageable, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));
        checkPageSize(pageable.getPageSize());

        // 채팅방 참여멤버만 메시지 조회 가능
        boolean memberExists = chatRoom.getChatRoomMembers().stream()
                .anyMatch(joinedMember -> joinedMember.getMember().getId().equals(loginUser.getId()));
        if (!memberExists) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }

        // 해당 채팅방 내의 모든 메시지 읽음 처리 & 해당 채팅방 메시지 가져오기
        readAllChatMessages(loginUser.getId(), chatRoomId);
        Page<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(chatRoomId, pageable);

        // MongoDB에서 메시지 내용 불러오기
        List<String> stringMessageIds = chatMessages.stream()
                .map(chatMessage -> String.valueOf(chatMessage.getId()))
                .collect(Collectors.toList());
        Map<Long, String> messageContentMap = messageContentRepository.findByIdIn(stringMessageIds)
                .stream()
                .filter(Objects::nonNull) // null 값 필터링
                .collect(Collectors.toMap(
                        message -> Long.parseLong(message.getId()),
                        message -> Objects.requireNonNullElse(message.getContent(), "") // null이면 빈 문자열 처리
                ));
        return chatMessages.map(chatMessage -> {
            String content = messageContentMap.getOrDefault(chatMessage.getId(), ""); // 없으면 빈 문자열
            return convertToChatMessageDto(chatMessage, content);
        });
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
    public void writeChatMessage(Long chatRoomId, @Valid ChatMessageForm chatMessageForm, Long memberId) {
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
        String content = (chatMessageForm != null) ? chatMessageForm.getContent() : "";
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
                    String imageUrl = s3Service.uploadFile(imageFile, "commPost-images");
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
        eventPublisher.publishEvent(new ChatMessageCreatedEvent(chatRoomId, chatMessageDto));
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
    public void readAllChatMessages(Long memberId, Long chatRoomId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));

        // 채팅방의 모든 메시지 ID 조회 (ID만 가져와서 가볍게 처리)
        List<Long> messageIds = chatMessageRepository.findAllMessageIdsByChatRoomId(chatRoomId);
        if (messageIds.isEmpty()) {
            return; // 메시지가 없으면 처리할 게 없음
        }

        // 이미 읽은 메시지 ID들 조회
        List<Long> alreadyReadMessageIds = chatMessageReadRepository.findMessageIdsByMemberIdAndChatRoomId(memberId, chatRoomId);

        // 아직 읽지 않은 메시지 ID 추출
        List<Long> unreadMessageIds = messageIds.stream()
                .filter(id -> !alreadyReadMessageIds.contains(id))
                .toList();

        // 읽음 기록 저장
        List<ChatMessageRead> readRecords = (List<ChatMessageRead>) unreadMessageIds.stream()
                .map(messageId -> ChatMessageRead.builder()
                        .chatMessage(ChatMessage.builder().id(messageId).build())
                        .member(member)
                        .readAt(LocalDateTime.now())
                        .build())
                .toList();
        chatMessageReadRepository.saveAll(readRecords);

        // chatRoomMember 상태 업데이트 (lastReadMessageId)
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberIdAndChatRoomId(chatRoomId, memberId);
        Long lastReadMessageId = messageIds.stream()
                .max(Long::compareTo)
                .orElse(null);
        chatRoomMember.setLastReadMessageId(lastReadMessageId);
        chatRoomMemberRepository.save(chatRoomMember);
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
                .readCount(unreadCount)
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
