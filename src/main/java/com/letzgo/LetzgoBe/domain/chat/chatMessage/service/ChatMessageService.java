package com.letzgo.LetzgoBe.domain.chat.chatMessage.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.ChatMessageResponse;
import com.letzgo.LetzgoBe.global.common.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatMessageService {
    // 메시지 읽음 처리
    void readChatMessage(Long messageId, Long memberId);

    // 해당 채팅방의 이전 메시지 가져오기
    PageResponse<ChatMessageResponse> findByChatRoomId(Long chatRoomId, Pageable pageable, CurrentUserDto loginUser);

    // 해당 채팅방에서 메시지 검색(내용)
    PageResponse<ChatMessageResponse> searchByKeyword(Long chatRoomId, String keyword, Pageable pageable, CurrentUserDto loginUser);

    // 해당 채팅방에서 메시지 생성
    ChatMessageResponse writeChatMessage(Long chatRoomId, String content, Long memberId);

    // 해당 채팅방에서 이미지 메시지 생성
    void writeImageMessage(Long chatRoomId, List<MultipartFile> imageFiles, CurrentUserDto loginUser);

    // 해당 메시지 삭제
    void deleteChatMessage(Long messageId, CurrentUserDto loginUser);

    // 해당 채팅방의 모든 메시지 삭제
    void deleteAllChatMessages(Long chatRoomId);

    // 해당 멤버가 작성한 모든 메시지 삭제
    void deleteMembersAllChatMessages(Long memberId);
}
