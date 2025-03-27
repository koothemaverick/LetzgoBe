package com.letzgo.LetzgoBe.domain.chat.chatMessage.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.req.ChatMessageForm;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.res.ChatMessageDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessage;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessagePage;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.MessageContent;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RabbitTemplate rabbitTemplate;
    private final MessageContentRepository messageContentRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final S3Service s3Service;

    // 해당 채팅방의 메시지 실시간 조회 시작
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
        readAllChatMessages(loginUser.getId(), chatRoom, chatRoomId);
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

    // 해당 채팅방에서 메시지 검색(닉네임/내용)
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
    public void writeChatMessage(Long chatRoomId, @Valid ChatMessageForm chatMessageForm, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));

        // 채팅방 참여 멤버만 메시지 생성 가능
        boolean memberExists = chatRoom.getChatRoomMembers().stream()
                .anyMatch(joinedMember -> joinedMember.getMember().getId().equals(loginUser.getId()));
        if (!memberExists) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }

        Long inactiveMemberNum = countInactiveMembers(chatRoomId);
        ChatMessage chatMessage = ChatMessage.builder()
                .member(loginUser.ConvertToMember())
                .chatRoom(chatRoom)
                .readCount(inactiveMemberNum)
                .build();
        chatMessageRepository.save(chatMessage);

        // MongoDB에 메시지 본문 저장
        String content = (chatMessageForm != null) ? chatMessageForm.getContent() : "";
        MessageContent messageContent = MessageContent.builder()
                .id(String.valueOf(chatMessage.getId()))
                .content(content)
                .build();
        messageContentRepository.save(messageContent);

        rabbitTemplate.convertAndSend("amq.topic", "chatRoom" + chatRoomId + "MessageCreated",
                convertToChatMessageDto(chatMessage, messageContent.getContent()));
    }

    // 해당 채팅방에서 이미지 메시지 생성
    @Override
    @Transactional
    public ChatMessageDto writeImageMessage(Long chatRoomId, List<MultipartFile> imageFiles, LoginUserDto loginUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ServiceException(ReturnCode.CHATROOM_NOT_FOUND));

        // 채팅방 참여 멤버만 메시지 생성 가능
        boolean memberExists = chatRoom.getChatRoomMembers().stream()
                .anyMatch(joinedMember -> joinedMember.getMember().getId().equals(loginUser.getId()));
        if (!memberExists) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }

        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new ServiceException(ReturnCode.FILE_UPLOAD_ERROR);
        }

        if (imageFiles.size() > 5) {
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

        Long inactiveMemberNum = countInactiveMembers(chatRoomId);
        ChatMessage chatMessage = ChatMessage.builder()
                .member(loginUser.ConvertToMember())
                .chatRoom(chatRoom)
                .imageUrls(imageUrls)
                .readCount(inactiveMemberNum)
                .build();
        chatMessageRepository.save(chatMessage);
        return convertToChatMessageDto(chatMessage, null);
    }

    // 해당 메시지 삭제
    @Override
    @Transactional
    public void deleteChatMessage(Long messageId, LoginUserDto loginUser) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId).orElseThrow(() -> new ServiceException(ReturnCode.CHATMESSAGE_NOT_FOUND));
        // 작성자만 삭제 가능
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

    // 해당 채팅방의 메시지 실시간 조회 중단
    @Override
    @Transactional
    public void updateLastReadMessage(Long chatRoomId, LoginUserDto loginUser) {
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberIdAndChatRoomId(loginUser.getId(), chatRoomId);
        chatRoomMember.setActive(false);

        ChatMessage lastReadMessage = chatMessageRepository.findTopByChatRoomIdOrderByIdDesc(chatRoomId);
        chatRoomMember.setLastReadMessageId(lastReadMessage != null ? lastReadMessage.getId() : null);
        chatRoomMemberRepository.save(chatRoomMember);
    }

    // 해당 채팅방 내의 모든 메시지 읽음 처리
    @Transactional
    public void readAllChatMessages(Long currentMemberId, ChatRoom chatRoom, Long chatRoomId){
        ChatRoomMember chatRoomMember = chatRoom.getChatRoomMembers().stream()
                .filter(member -> member.getMember().getId().equals(currentMemberId))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));

        // 채팅방 미참여중인 멤버가 참여 했을때만 실행
        if (!chatRoomMember.isActive()) {
            Long lastReadMessageId = chatRoomMember.getLastReadMessageId();
            List<ChatMessage> chatMessages;
            if (lastReadMessageId == null) {
                // 처음 들어오는 경우, 모든 메시지의 readCount -1 처리
                chatMessages = chatMessageRepository.findByChatRoom(chatRoom);
            } else {
                // 마지막으로 읽은 메시지 이후에 생성된 메시지들만 readCount -1 처리
                chatMessages = chatMessageRepository.findByChatRoomIdAndIdGreaterThan(chatRoomId, lastReadMessageId);
            }
            List<ChatMessage> updatedMessages = chatMessages.stream()
                    .peek(chatMessage -> {
                        if (chatMessage.getReadCount() > 0) {
                            chatMessage.setReadCount(chatMessage.getReadCount() - 1);
                        }
                    })
                    .collect(Collectors.toList());
            chatMessageRepository.saveAll(updatedMessages);
        }
        chatRoomMember.setActive(true);

        // 마지막으로 읽은 메시지ID 초기화
        ChatMessage lastReadMessage = chatMessageRepository.findTopByChatRoomIdOrderByIdDesc(chatRoomId);
        chatRoomMember.setLastReadMessageId(lastReadMessage != null ? lastReadMessage.getId() : null);
        chatRoomMemberRepository.save(chatRoomMember);
    }

    // 해당 채팅방에 실시간 참여중이 아닌 인원수(lastReadMessageId=null인 chatRoomMember 인원수)
    @Transactional(readOnly = true)
    public Long countInactiveMembers(Long chatRoomId){
        return chatRoomMemberRepository.countInActiveMembers(chatRoomId);
    }

    // 요청 메시지 수 제한
    private void checkPageSize(int pageSize) {
        int maxPageSize = ChatMessagePage.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new ServiceException(ReturnCode.PAGE_REQUEST_FAIL);
        }
    }

    // ChatMessage를 ChatMessageDto로 변환
    private ChatMessageDto convertToChatMessageDto(ChatMessage chatMessage, String content) {
        return ChatMessageDto.builder()
                .id(chatMessage.getId())
                .memberId(chatMessage.getMember().getId())
                .nickname(chatMessage.getMember().getNickname())
                .profileImageUrl(chatMessage.getMember().getProfileImageUrl())
                .content(content)
                .imageUrls(chatMessage.getImageUrls())
                .readCount(chatMessage.getReadCount())
                .createDate(chatMessage.getCreateDate())
                .build();
    }
}
