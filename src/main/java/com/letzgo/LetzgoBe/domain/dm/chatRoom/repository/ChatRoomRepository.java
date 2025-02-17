package com.letzgo.LetzgoBe.domain.dm.chatRoom.repository;

import com.letzgo.LetzgoBe.domain.dm.chatRoom.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 사용자의 채팅방 목록에서 검색(여기서 채팅방 생성 가능)
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.joinUserList u WHERE u.nickName LIKE %:keyword%")
    Page<ChatRoom> searchByUserUsername(@Param("keyword") String keyword, Pageable pageable);

    // 사용자 아이디로 채팅방 가져오기
    ChatRoom findByUserId(Long userId);
}
