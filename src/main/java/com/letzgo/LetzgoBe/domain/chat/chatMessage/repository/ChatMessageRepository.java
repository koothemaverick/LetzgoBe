package com.letzgo.LetzgoBe.domain.chat.chatMessage.repository;

import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessage;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 해당 채팅방의 메시지 페이지 조회
    @Query("""
       SELECT m FROM ChatMessage m
       JOIN FETCH m.member
       JOIN FETCH m.chatRoom r
       JOIN FETCH r.chatRoomMembers
       WHERE r.id = :chatRoomId
    """)
    Page<ChatMessage> findByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    // 해당 채팅방의 메시지 리스트 조회
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    // 해당 채팅방의 메시지ID 리스트 조회
    @Query("SELECT m.id FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId")
    List<Long> findAllMessageIdsByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 가장 최근 메시지의 ID를 가져오는 메서드
    @Query("""
        SELECT cm.id
        FROM ChatMessage cm
        WHERE cm.chatRoom.id = :chatRoomId
        ORDER BY cm.createdAt DESC
    """)
    List<Long> findLatestMessageIdsByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    // 해당 멤버가 작성한 모든 메시지 삭제
    List<ChatMessage> findByMemberId(Long memberId);

    // 본인이 안 읽은 메시지 수 계산
    @Query("""
       SELECT COUNT(cm) 
       FROM ChatMessage cm
       WHERE cm.chatRoom.id = :chatRoomId
       AND NOT EXISTS (
           SELECT 1 
           FROM ChatMessageRead cmr
           WHERE cmr.chatMessage.id = cm.id 
           AND cmr.member.id = :memberId
       )
       """)
    Long countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    @Query("""
           SELECT m FROM ChatMessage m
           JOIN FETCH m.member mem
           JOIN FETCH m.chatRoom r
           LEFT JOIN FETCH r.chatRoomMembers
           WHERE r.id = :chatRoomId
           """)
    Page<ChatMessage> findByChatRoomIdWithMembers(@Param("chatRoomId") Long chatRoomId, Pageable pageable);
}
