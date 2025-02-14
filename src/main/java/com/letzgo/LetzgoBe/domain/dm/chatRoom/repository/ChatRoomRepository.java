package com.letzgo.LetzgoBe.domain.dm.chatRoom.repository;

import com.letzgo.LetzgoBe.domain.dm.chatRoom.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Page<ChatRoom> findByjoinMemberIdNickNameListContaining(String keyword, Pageable pageable);
}
