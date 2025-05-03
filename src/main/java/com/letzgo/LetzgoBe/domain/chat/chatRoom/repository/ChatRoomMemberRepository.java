package com.letzgo.LetzgoBe.domain.chat.chatRoom.repository;

import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    // memberId, chatRoomID로 ChatRoomMember 찾기
    ChatRoomMember findByMemberIdAndChatRoomId(Long currentMemberId, Long chatRoomId);
}
