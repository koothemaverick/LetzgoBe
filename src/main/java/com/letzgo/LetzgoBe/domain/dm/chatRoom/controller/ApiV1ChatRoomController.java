package com.letzgo.LetzgoBe.domain.dm.chatRoom.controller;

import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.res.DetailChatRoomDto;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.req.CreateChatRoomForm;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.req.UpdateChatRoomForm;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.domain.user.member.entity.Member;
import com.letzgo.LetzgoBe.global.webMvc.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("letzgo/api/v1/post")
@RequiredArgsConstructor
public class ApiV1ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 사용자의 모든 채팅방 조회
    @GetMapping("/chat-rooms")
    public Page<DetailChatRoomDto> getAllRooms(Pageable pageable, @LoginUser Member loginUser){
        return chatRoomService.findAll(pageable);
    }

    // 사용자의 채팅방/팔로워 검색(여기서 채팅방 생성 가능)
    @GetMapping("/chat-rooms/search")
    public Page<DetailChatRoomDto> searchRooms(@RequestParam("keyword") String keyword, Pageable pageable, @LoginUser Member loginUser){
        return chatRoomService.searchByKeyword(keyword, pageable);
    }

    // 해당 유저와 채팅방 생성
    @PostMapping("/chat-room/{userId}")
    public ResponseEntity<String> createRoom(@PathVariable("userId") String matchUserId, @RequestBody @Valid CreateChatRoomForm createChatRoomForm, @LoginUser Member loginUser) {
        chatRoomService.createChatRoom(matchUserId, createChatRoomForm, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("모임이 성공적으로 만들어졌습니다.");
    }

    // 해당 채팅방 수정(방장만 가능)
    @PutMapping("/{chatRoomId}")
    public ResponseEntity<String> updateRoom(@PathVariable("chatRoomId") String chatRoomId, @RequestBody @Valid UpdateChatRoomForm updateChatRoomForm, @LoginUser Member loginUser) {
        chatRoomService.updateChatRoom(chatRoomId, updateChatRoomForm, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("모임이 성공적으로 수정되었습니다.");
    }

    // 해당 채팅방 나가기(방장이 나가는 경우 해당 모임채팅방 삭제)
    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<String> leaveChatRoom(@PathVariable("chatRoomId") String chatRoomId, @LoginUser Member loginUser) {
        chatRoomService.leaveChatRoom(chatRoomId, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("성공적으로 모임채팅방을 나갔습니다.");
    }

    // 해당 채팅방의 참여자 강퇴
    @GetMapping("/unqualify-chat-room/{chatRoomId}/{userId}")
    public ResponseEntity<String> unqualifyChatRoom(@PathVariable("chatRoomId") String chatRoomId, @PathVariable("memberId") String unqualifyUserId, @LoginUser Member loginUser) {
        chatRoomService.unqualifyChatRoom(chatRoomId, unqualifyUserId, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("성공적으로 사용자를 강퇴했습니다.");
    }

    // 해당 모임채팅방에서 참여자에게 방장권한 위임
    @GetMapping("/delegate-chat-room/{chatRoomId}/{userId}")
    public ResponseEntity<String> delegateChatRoom(@PathVariable("chatRoomId") String chatRoomId, @PathVariable("memberId") String delegateUserId, @LoginUser Member loginUser) {
        chatRoomService.delegateChatRoom(chatRoomId, delegateUserId, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("성공적으로 방장권한을 위임했습니다.");
    }
}
