package com.letzgo.LetzgoBe.domain.chat.chatRoom.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.ChatRoomDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {
    // 사용자의 모든 채팅방 조회
    Page<ChatRoomDto> getChatRoom(Pageable pageable, LoginUserDto loginUser);

    // 채팅방 생성(DM/그룹)
    ChatRoomDto addChatRoom(@Valid ChatRoomForm chatRoomForm, LoginUserDto loginUser);

    // 채팅방 이름 수정(그룹)
    void updateChatRoomTitle(Long chatRoomId, @Valid ChatRoomForm chatRoomForm, LoginUserDto loginUser);

    // 채팅방에 초대(그룹)
    void inviteChatRoomMember(Long chatRoomId, @Valid ChatRoomForm chatRoomForm, LoginUserDto loginUser);

    // 채팅방에서 강퇴(그룹) / 방장권한
    void kickOutChatRoomMember(Long chatRoomId, @Valid ChatRoomForm chatRoomForm, LoginUserDto loginUser);

    // 채팅방 나가기(DM/그룹)
    void leaveChatRoomMember(Long chatRoomId, LoginUserDto loginUser);
}
