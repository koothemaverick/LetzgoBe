package com.letzgo.LetzgoBe.domain.chat.chatRoom.controller;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.ChatRoomResponse;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomRequest;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomPage;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest-api/v1/chat-room")
@RequiredArgsConstructor
@Tag(name = "ChatRoom", description = "채팅방 API")
public class ApiV1ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅방 리스트 조회(DM/그룹) [참여자 권한]
    @GetMapping
    @Operation(summary = "채팅방 리스트 조회", description = "채팅방 리스트를 조회합니다.")
    public ApiResponse<List<ChatRoomResponse>> getChatRoom(@ModelAttribute ChatRoomPage request, @CurrentUser CurrentUserDto currentUser){
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(chatRoomService.getChatRoom(pageable, currentUser));
    }

    // 채팅방 생성(DM/그룹)
    @PostMapping
    @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다.")
    public ApiResponse<ChatRoomResponse> addChatRoom(@RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto currentUser){
        return ApiResponse.success(chatRoomService.addChatRoom(chatRoomRequest, currentUser));
    }

    // 채팅방 이름 수정(그룹) [참여자 권한]
    @PatchMapping("/title/{chatRoomId}")
    @Operation(summary = "채팅방 이름 수정", description = "채팅방 이름을 수정합니다.")
    public ApiResponse<Void> updateChatRoomTitle(@PathVariable("chatRoomId") Long chatRoomId,
                                                   @RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto currentUser){
        chatRoomService.updateChatRoomTitle(chatRoomId, chatRoomRequest, currentUser);
        return ApiResponse.success();
    }

    // 채팅방에 초대(그룹) [참여자 권한]
    @PatchMapping("/group/{chatRoomId}")
    @Operation(summary = "채팅방 초대", description = "채팅방에 초대합니다.")
    public ApiResponse<Void> inviteChatRoomMember(@PathVariable("chatRoomId") Long chatRoomId,
                                                    @RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto currentUser){
        chatRoomService.inviteChatRoomMember(chatRoomId, chatRoomRequest, currentUser);
        return ApiResponse.success();
    }

    // 방장 권한 위임(그룹) [방장 권한]
    @PatchMapping("/groupHost/{chatRoomId}")
    @Operation(summary = "방장 권한 위임", description = "방장 권한을 위임합니다.")
    public ApiResponse<Void> delegateChatRoomManager(@PathVariable("chatRoomId") Long chatRoomId,
                                                       @RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto currentUser){
        chatRoomService.delegateChatRoomManager(chatRoomId, chatRoomRequest, currentUser);
        return ApiResponse.success();
    }

    // 채팅방에서 강퇴(그룹) [방장 권한]
    @DeleteMapping("/group/{chatRoomId}")
    @Operation(summary = "채팅방 강퇴", description = "채팅방에서 강퇴합니다.")
    public ApiResponse<Void> kickOutChatRoomMember(@PathVariable("chatRoomId") Long chatRoomId,
                                                     @RequestBody @Valid ChatRoomRequest chatRoomRequest, @CurrentUser CurrentUserDto currentUser){
        chatRoomService.kickOutChatRoomMember(chatRoomId, chatRoomRequest, currentUser);
        return ApiResponse.success();
    }

    // 채팅방 나가기(DM/그룹) [참여자 권한]
    @DeleteMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 나가기", description = "채팅방에서 나갑니다.")
    public ApiResponse<Void> leaveChatRoom(@PathVariable("chatRoomId") Long chatRoomId, @CurrentUser CurrentUserDto currentUser){
        chatRoomService.leaveChatRoomMember(chatRoomId, currentUser);
        return ApiResponse.success();
    }
}
