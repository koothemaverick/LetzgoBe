package com.letzgo.LetzgoBe.domain.dm.chatRoom.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.repository.UserRepository;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.res.ChatRoomList;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.entity.ChatRoom;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.repository.ChatRoomRepository;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 사용자의 모든 채팅방 조회
    @Override
    @Transactional
    public Page<ChatRoomList> findAll(Pageable pageable, User loginUser) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findAll(pageable);
        return chatRooms.map(ChatRoomList::from);
    }

    // 사용자의 채팅방 목록에서 검색(여기서 채팅방 생성 가능)
    @Override
    @Transactional
    public Page<ChatRoomList> searchByKeyword(String keyword, Pageable pageable, User loginUser){
        Page<ChatRoom> chatRooms = chatRoomRepository.searchByUserUsername(keyword, pageable);
        return chatRooms.map(ChatRoomList::from);
    }

    // 선택한 유저와 채팅방(1:1, 단체) 생성
    @Override
    @Transactional
    public void createChatRoom(ChatRoomForm chatRoomForm, User loginUser) {
        // ChatRoomUser Dto -> User 변환
        List<User> users = chatRoomForm.getJoinUserList().stream()
                .map(chatRoomUser -> userRepository.findById(chatRoomUser.getId())
                        .orElseThrow(() -> new RuntimeException("User not found: " + chatRoomUser.getId())))
                .collect(Collectors.toList());

        ChatRoom chatRoom = ChatRoom.builder()
                        .user(loginUser)
                        .joinUserList(users)
                        .build();

        chatRoomRepository.save(chatRoom);
    }

    // 선택한 유저 초대하기
    @Override
    @Transactional
    public void inviteUser(ChatRoomForm chatRoomForm, User loginUser){
        // ChatRoomUser Dto -> User 변환
        List<User> usersToInvite = chatRoomForm.getJoinUserList().stream()
                .map(chatRoomUser -> userRepository.findById(chatRoomUser.getId())
                        .orElseThrow(() -> new RuntimeException("User not found: " + chatRoomUser.getId())))
                .collect(Collectors.toList());

        // 현재 참여자 목록 가져오기
        ChatRoom chatRoom = chatRoomRepository.findByUserId(loginUser.getId());
        List<User> currentJoinUserList = chatRoom.getJoinUserList();

        // 새로운 사용자들을 현재 참여자 목록에 추가
        for (User user : usersToInvite) {
            if (!currentJoinUserList.contains(user)) {
                currentJoinUserList.add(user);
            }
        }
        chatRoom.setJoinUserList(currentJoinUserList);
        chatRoomRepository.save(chatRoom);
    }

    // 해당 채팅방 나가기
    @Override
    @Transactional
    public void leaveChatRoom(String chatRoomId, User loginUser){
        // chatRoomId로 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(chatRoomId))
                .orElseThrow(() -> new RuntimeException("ChatRoom not found: " + chatRoomId));

        // 현재 참여자 목록에서 loginUser 제거
        List<User> currentJoinUserList = chatRoom.getJoinUserList();
        if (currentJoinUserList.contains(loginUser)) {
            currentJoinUserList.remove(loginUser);
            chatRoom.setJoinUserList(currentJoinUserList);
            chatRoomRepository.save(chatRoom);

            // 참여자 목록이 비어 있으면 채팅방 삭제
            if (currentJoinUserList.isEmpty()) {
                chatRoomRepository.delete(chatRoom);
            }
        } else {
            throw new RuntimeException("User not a member of the chat room: " + loginUser.getId());
        }
    }
}
