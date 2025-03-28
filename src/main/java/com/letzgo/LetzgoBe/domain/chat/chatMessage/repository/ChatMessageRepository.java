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
    Page<ChatMessage> findByChatRoomId(Long chatRoomId, Pageable pageable);

    // 해당 채팅방의 메시지 리스트 조회
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    // 가장 최근 메시지 조회
    ChatMessage findTopByChatRoomIdOrderByIdDesc(Long chatRoomId);

    // 해당 채팅방의 해당 메시지 이후에 작성된 메시지 리스트 조회
    List<ChatMessage> findByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long lastReadMessageId);

    // 가장 최근 메시지의 ID를 가져오는 메서드
    @Query("SELECT cm.id FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId ORDER BY cm.createDate DESC LIMIT 1")
    Optional<Long> findLatestMessageIdByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 해당 멤버가 작성한 모든 메시지 삭제
    List<ChatMessage> findByMemberId(Long memberId);
}
