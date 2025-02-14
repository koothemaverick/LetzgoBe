package com.letzgo.LetzgoBe.domain.dm.chatRoom.service;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.res.ChatRoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomService {
    // 사용자의 모든 채팅방 조회
    Page<ChatRoomDto> findAll(Pageable pageable, User loginUser);

    // 사용자의 채팅방/팔로워 검색(여기서 채팅방 생성 가능)
    Page<ChatRoomDto> searchByKeyword(String keyword, Pageable pageable, User loginUser);

    // 선택한 유저와 채팅방(1:1, 단체) 생성
    void createChatRoom(ChatRoomForm createChatRoomForm, User loginUser);

    // 선택한 유저 초대하기
    void inviteUser(ChatRoomForm inviteChatRoomForm, User loginUser);

    // 해당 채팅방 나가기
    void leaveChatRoom(String chatRoomId, User loginUser);
}
