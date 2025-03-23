package com.letzgo.LetzgoBe.domain.chat.chatRoom.repository;

import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    // 해당 채팅방에 실시간 참여중이 아닌 인원수
    @Query("SELECT COUNT(cm) FROM ChatRoomMember cm WHERE cm.chatRoom.id = :chatRoomId AND cm.active IS false")
    Long countInActiveMembers(@Param("chatRoomId") Long chatRoomId);

    // memberId, chatRoomID로 ChatRoomMember 찾기
    ChatRoomMember findByMemberIdAndChatRoomId(Long currentMemberId, Long chatRoomId);
}
