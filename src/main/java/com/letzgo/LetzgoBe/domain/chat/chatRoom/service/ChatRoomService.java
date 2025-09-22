package com.letzgo.LetzgoBe.domain.chat.chatRoom.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomRequest;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.ChatRoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {
    // 사용자의 모든 채팅방 조회
    Page<ChatRoomResponse> getChatRoom(Pageable pageable, LoginUserDto loginUser);

    // 채팅방 생성(DM/그룹)
    ChatRoomResponse addChatRoom(ChatRoomRequest chatRoomRequest, LoginUserDto loginUser);

    // 채팅방 이름 수정(그룹)
    void updateChatRoomTitle(Long chatRoomId, ChatRoomRequest chatRoomRequest, LoginUserDto loginUser);

    // 채팅방에 초대(그룹)
    void inviteChatRoomMember(Long chatRoomId, ChatRoomRequest chatRoomRequest, LoginUserDto loginUser);

    // 방장 권한 위임(그룹)
    void delegateChatRoomManager(Long chatRoomId, ChatRoomRequest chatRoomRequest, LoginUserDto loginUser);

    // 채팅방에서 강퇴(그룹)
    void kickOutChatRoomMember(Long chatRoomId, ChatRoomRequest chatRoomRequest, LoginUserDto loginUser);

    // 채팅방 나가기(DM/그룹)
    void leaveChatRoomMember(Long chatRoomId, LoginUserDto loginUser);

    // 멤버가 참여중인 모든 채팅방 나가기(DM/그룹)
    void leaveAllChatRooms(Long memberId);
}
