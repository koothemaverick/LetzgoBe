package com.letzgo.LetzgoBe.domain.dm.chatRoom.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.entity.ChatRoom;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl {
    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional
    public Page<ChatRoomDto> findAll(Pageable pageable, User loginUser) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findAll(pageable);
        return chatRooms.map(this::convertToChatRoomDto);
    }

    @Override
    @Transactional
    public Page<ChatRoomDto> searchByKeyword(String keyword, Pageable pageable, User loginUser){
        Page<ChatRoom> chatRooms = chatRoomRepository.findByjoinMemberIdNickNameListContaining(keyword, pageable);
        return chatRooms.map(this::convertToChatRoomDTO);
    }

    @Override
    @Transactional
    public void createChatRoom(ChatRoomForm ChatRoomForm, User loginUser) {
        ChatRoom chatRoom = ChatRoom.builder()
                        .user(loginUser)
                                .joinMemberIdNickNameList()
                                        .roomMemberLimit(100L);
        chatRoomRepository.save(chatRoom);
    }

    @Override
    @Transactional
    public void inviteUser(ChatRoomForm chatRoomForm, User loginUser){

    }

    @Override
    @Transactional
    public void leaveChatRoom(String chatRoomId, User loginUser){

    }
}
