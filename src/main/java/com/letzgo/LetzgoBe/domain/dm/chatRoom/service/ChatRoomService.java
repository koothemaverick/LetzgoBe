package com.letzgo.LetzgoBe.domain.dm.chatRoom.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.res.ChatRoomList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {
    // 사용자의 모든 채팅방 조회
    Page<ChatRoomList> findAll(Pageable pageable, LoginUserDto loginUser);

    // 사용자의 채팅방 목록에서 검색(여기서 채팅방 생성 가능)
    Page<ChatRoomList> searchByKeyword(String keyword, Pageable pageable, LoginUserDto loginUser);

    // 선택한 유저와 채팅방(1:1, 단체) 생성
    void createChatRoom(ChatRoomForm createChatRoomForm, LoginUserDto loginUser);

    // 선택한 유저 초대하기
    void inviteUser(ChatRoomForm inviteChatRoomForm, LoginUserDto loginUser);

    // 해당 채팅방 나가기
    void leaveChatRoom(String chatRoomId, LoginUserDto loginUser);
}
