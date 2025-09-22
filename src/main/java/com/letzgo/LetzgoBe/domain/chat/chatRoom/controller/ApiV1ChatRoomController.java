package com.letzgo.LetzgoBe.domain.chat.chatRoom.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.ChatRoomResponse;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomRequest;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomPage;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.common.response.LetzgoPage;
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
    public ApiResponse<ChatRoomResponse> getChatRoom(@ModelAttribute ChatRoomPage request, @LoginUser LoginUserDto loginUser){
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(LetzgoPage.of(chatRoomService.getChatRoom(pageable, loginUser)));
    }

    // 채팅방 생성(DM/그룹)
    @PostMapping
    public ApiResponse<ChatRoomResponse> addChatRoom(@RequestBody @Valid ChatRoomRequest chatRoomRequest, @LoginUser LoginUserDto loginUser){
        return ApiResponse.of(chatRoomService.addChatRoom(chatRoomRequest, loginUser));
    }

    // 채팅방 이름 수정(그룹) [참여자 권한]
    @PutMapping("/title/{chatRoomId}")
    public ApiResponse<String> updateChatRoomTitle(@PathVariable("chatRoomId") Long chatRoomId,
                                                   @RequestBody @Valid ChatRoomRequest chatRoomRequest, @LoginUser LoginUserDto loginUser){
        chatRoomService.updateChatRoomTitle(chatRoomId, chatRoomRequest, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 채팅방에 초대(그룹) [참여자 권한]
    @PutMapping("/group/{chatRoomId}")
    public ApiResponse<String> inviteChatRoomMember(@PathVariable("chatRoomId") Long chatRoomId,
                                                    @RequestBody @Valid ChatRoomRequest chatRoomRequest, @LoginUser LoginUserDto loginUser){
        chatRoomService.inviteChatRoomMember(chatRoomId, chatRoomRequest, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 방장 권한 위임(그룹) [방장 권한]
    @PostMapping("/group/{chatRoomId}")
    public ApiResponse<String> delegateChatRoomManager(@PathVariable("chatRoomId") Long chatRoomId,
                                                       @RequestBody @Valid ChatRoomRequest chatRoomRequest, @LoginUser LoginUserDto loginUser){
        chatRoomService.delegateChatRoomManager(chatRoomId, chatRoomRequest, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 채팅방에서 강퇴(그룹) [방장 권한]
    @DeleteMapping("/group/{chatRoomId}")
    public ApiResponse<String> kickOutChatRoomMember(@PathVariable("chatRoomId") Long chatRoomId,
                                                     @RequestBody @Valid ChatRoomRequest chatRoomRequest, @LoginUser LoginUserDto loginUser){
        chatRoomService.kickOutChatRoomMember(chatRoomId, chatRoomRequest, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 채팅방 나가기(DM/그룹) [참여자 권한]
    @DeleteMapping("/{chatRoomId}")
    public ApiResponse<String> leaveChatRoom(@PathVariable("chatRoomId") Long chatRoomId, @LoginUser LoginUserDto loginUser){
        chatRoomService.leaveChatRoomMember(chatRoomId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }
}
