package com.letzgo.LetzgoBe.domain.chat.chatMessage.repository;

import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessageRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageReadRepository extends JpaRepository<ChatMessageRead, Long> {
    // 이미 읽은 메시지 ID들 조회
    @Query("SELECT r.chatMessage.id FROM ChatMessageRead r WHERE r.member.id = :memberId AND r.chatMessage.chatRoom.id = :chatRoomId")
    List<Long> findMessageIdsByMemberIdAndChatRoomId(@Param("memberId") Long memberId, @Param("chatRoomId") Long chatRoomId);

    // 메시지 읽은 수 조회
    Long countByChatMessageId(Long chatMessageId);

    // DB에서 읽은 기록이 있는지 확인
    Optional<ChatMessageRead> findByChatMessageIdAndMemberId(Long chatMessageId, Long memberId);
}
