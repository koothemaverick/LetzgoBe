package com.letzgo.LetzgoBe.domain.chat.chatMessage.service;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.ChatMessageResponse;
import com.letzgo.LetzgoBe.global.common.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatMessageService {
    // 메시지 읽음 처리
    void readChatMessage(Long messageId, Long memberId);

    // 채팅방의 이전 메시지 가져오기 & 모든 메시지 읽음 처리[참여자 권한]
    PageResponse<ChatMessageResponse> findByChatRoomId(Long chatRoomId, Pageable pageable, CurrentUserDto currentUser);

    // 채팅방에서 메시지 검색(내용) [참여자 권한]
    PageResponse<ChatMessageResponse> searchByKeyword(Long chatRoomId, String keyword, Pageable pageable, CurrentUserDto currentUser);

    // 채팅방에서 메시지 생성
    ChatMessageResponse writeChatMessage(Long chatRoomId, String content, Long memberId);

    // 채팅방에서 이미지 메시지 생성 [참여자 권한]
    void writeImageMessage(Long chatRoomId, List<MultipartFile> imageFiles, CurrentUserDto currentUser);

    // 메시지 삭제 [참여자 권한]
    void deleteChatMessage(Long messageId, CurrentUserDto currentUser);

    // 채팅방의 모든 메시지 삭제
    void deleteAllChatMessages(Long chatRoomId);

    // 멤버가 작성한 모든 메시지 삭제
    void deleteMembersAllChatMessages(Long memberId);
}
