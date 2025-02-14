package com.letzgo.LetzgoBe.domain.dm.chatRoom.controller;

import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.res.DetailChatRoomDto;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.req.UpdateChatRoomForm;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.global.webMvc.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("letzgo/restful/api/v1/post")
@RequiredArgsConstructor
public class ApiV1ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 사용자의 모든 채팅방 조회
    @GetMapping("/chat-rooms")
    public Page<ChatRoomDto> getAllRooms(Pageable pageable, @LoginUser User loginUser){
        return chatRoomService.findAll(pageable);
    }

    // 사용자의 채팅방/팔로워 검색(여기서 채팅방 생성 가능)
    @GetMapping("/chat-rooms/search")
    public Page<ChatRoomDto> searchRooms(@RequestParam("keyword") String keyword, Pageable pageable, @LoginUser User loginUser){
        return chatRoomService.searchByKeyword(keyword, pageable);
    }

    // 선택한 유저와 채팅방(1:1, 단체) 생성
    @PostMapping("/chat-room")
    public ResponseEntity<String> createChatRoom(@RequestBody @Valid ChatRoomForm chatRoomForm, @LoginUser User loginUser) {
        chatRoomService.createChatRoom(chatRoomForm, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("채팅방이 성공적으로 만들어졌습니다.");
    }

    // 선택한 유저 초대하기
    @PutMapping("/chat-room")
    public ResponseEntity<String> inviteUser(@RequestBody @Valid ChatRoomForm ChatRoomForm, @LoginUser User loginUser) {
        chatRoomService.inviteUser(ChatRoomForm, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("유저를 성공적으로 초대했습니다.");
    }

    // 해당 채팅방 나가기
    @DeleteMapping("/chat-room/{chatRoomId}")
    public ResponseEntity<String> leaveChatRoom(@PathVariable("chatRoomId") String chatRoomId, @LoginUser User loginUser) {
        chatRoomService.leaveChatRoom(chatRoomId, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("채팅방을 성공적으로 나갔습니다.");
    }
}
