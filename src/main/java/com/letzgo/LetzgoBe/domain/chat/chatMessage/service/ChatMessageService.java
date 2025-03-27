package com.letzgo.LetzgoBe.domain.chat.chatMessage.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.req.ChatMessageForm;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.res.ChatMessageDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatMessageService {
    // 해당 채팅방의 메시지 실시간 조회 시작
    Page<ChatMessageDto> findByChatRoomId(Long chatRoomId, Pageable pageable, LoginUserDto loginUser);

    // 해당 채팅방에서 메시지 검색(내용)
    Page<ChatMessageDto> searchByKeyword(Long chatRoomId, String keyword, Pageable pageable, LoginUserDto loginUser);

    // 해당 채팅방에서 메시지 생성
    void writeChatMessage(Long chatRoomId, @Valid ChatMessageForm chatMessageForm, LoginUserDto loginUser);

    // 해당 채팅방에서 이미지 메시지 생성
    ChatMessageDto writeImageMessage(Long chatRoomId, List<MultipartFile> imageFiles, LoginUserDto loginUser);

    // 해당 메시지 삭제
    void deleteChatMessage(Long messageId, LoginUserDto loginUser);

    // 해당 채팅방의 모든 메시지 삭제
    void deleteAllChatMessages(Long chatRoomId);

    // 해당 채팅방의 메시지 실시간 조회 중단
    void updateLastReadMessage(Long chatRoomId, LoginUserDto loginUser);
}
