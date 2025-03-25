package com.letzgo.LetzgoBe.domain.chat.chatRoom.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.ChatRoomDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomPage;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.common.response.Page;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest-api/v1/chat-room")
@RequiredArgsConstructor
public class ApiV1ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅방 목록 조회(DM/그룹) [참여자 권한]
    @GetMapping
    public ApiResponse<ChatRoomDto> getChatRoom(@ModelAttribute ChatRoomPage request, @LoginUser LoginUserDto loginUser){
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(Page.of(chatRoomService.getChatRoom(pageable, loginUser)));
    }

    // 채팅방 생성(DM/그룹) [회원 권한]
    @PostMapping
    public ApiResponse<ChatRoomDto> addChatRoom(@RequestBody @Valid ChatRoomForm chatRoomForm, @LoginUser LoginUserDto loginUser){
        return ApiResponse.of(chatRoomService.addChatRoom(chatRoomForm, loginUser));
    }

    // 채팅방 이름 수정(그룹) [참여자 권한]
    @PutMapping("/title/{chatRoomId}")
    public ApiResponse<String> updateChatRoomTitle(@PathVariable("chatRoomId") Long chatRoomId,
                                                   @RequestBody @Valid ChatRoomForm chatRoomForm, @LoginUser LoginUserDto loginUser){
        chatRoomService.updateChatRoomTitle(chatRoomId, chatRoomForm, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 채팅방에 초대(그룹) [참여자 권한]
    @PutMapping("/group/{chatRoomId}")
    public ApiResponse<String> inviteChatRoomMember(@PathVariable("chatRoomId") Long chatRoomId,
                                                    @RequestBody @Valid ChatRoomForm chatRoomForm, @LoginUser LoginUserDto loginUser){
        chatRoomService.inviteChatRoomMember(chatRoomId, chatRoomForm, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 채팅방에서 강퇴(그룹) [방장 권한]
    @DeleteMapping("/group/{chatRoomId}")
    public ApiResponse<String> kickOutChatRoomMember(@PathVariable("chatRoomId") Long chatRoomId,
                                                     @RequestBody @Valid ChatRoomForm chatRoomForm, @LoginUser LoginUserDto loginUser){
        chatRoomService.kickOutChatRoomMember(chatRoomId, chatRoomForm, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 채팅방 나가기(DM/그룹) [참여자 권한]
    @DeleteMapping("/{chatRoomId}")
    public ApiResponse<String> leaveChatRoomMember(@PathVariable("chatRoomId") Long chatRoomId, @LoginUser LoginUserDto loginUser){
        chatRoomService.leaveChatRoomMember(chatRoomId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }
}
