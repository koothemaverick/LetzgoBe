package com.letzgo.LetzgoBe.domain.chat.chatRoom.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.ChatRoomResponse;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomRequest;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomPage;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest-api/v1/chat-room")
@RequiredArgsConstructor
public class ApiV1ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅방 목록 조회(DM/그룹) [참여자 권한]
    @GetMapping
    public ApiResponse<List<ChatRoomResponse>> getChatRoom(@ModelAttribute ChatRoomPage request, @CurrentUser CurrentUserDto loginUser){
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(chatRoomService.getChatRoom(pageable, loginUser));
    }

    // 채팅방 생성(DM/그룹)
    @PostMapping
    public ApiResponse<ChatRoomResponse> addChatRoom(@RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto loginUser){
        return ApiResponse.success(chatRoomService.addChatRoom(chatRoomRequest, loginUser));
    }

    // 채팅방 이름 수정(그룹) [참여자 권한]
    @PatchMapping("/title/{chatRoomId}")
    public ApiResponse<Void> updateChatRoomTitle(@PathVariable("chatRoomId") Long chatRoomId,
                                                   @RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto loginUser){
        chatRoomService.updateChatRoomTitle(chatRoomId, chatRoomRequest, loginUser);
        return ApiResponse.success();
    }

    // 채팅방에 초대(그룹) [참여자 권한]
    @PatchMapping("/group/{chatRoomId}")
    public ApiResponse<Void> inviteChatRoomMember(@PathVariable("chatRoomId") Long chatRoomId,
                                                    @RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto loginUser){
        chatRoomService.inviteChatRoomMember(chatRoomId, chatRoomRequest, loginUser);
        return ApiResponse.success();
    }

    // 방장 권한 위임(그룹) [방장 권한]
    @PostMapping("/group/{chatRoomId}")
    public ApiResponse<Void> delegateChatRoomManager(@PathVariable("chatRoomId") Long chatRoomId,
                                                       @RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto loginUser){
        chatRoomService.delegateChatRoomManager(chatRoomId, chatRoomRequest, loginUser);
        return ApiResponse.success();
    }

    // 채팅방에서 강퇴(그룹) [방장 권한]
    @DeleteMapping("/group/{chatRoomId}")
    public ApiResponse<Void> kickOutChatRoomMember(@PathVariable("chatRoomId") Long chatRoomId,
                                                     @RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto loginUser){
        chatRoomService.kickOutChatRoomMember(chatRoomId, chatRoomRequest, loginUser);
        return ApiResponse.success();
    }

    // 채팅방 나가기(DM/그룹) [참여자 권한]
    @DeleteMapping("/{chatRoomId}")
    public ApiResponse<Void> leaveChatRoom(@PathVariable("chatRoomId") Long chatRoomId, @CurrentUser CurrentUserDto loginUser){
        chatRoomService.leaveChatRoomMember(chatRoomId, loginUser);
        return ApiResponse.success();
    }
}
