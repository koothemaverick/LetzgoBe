package com.letzgo.LetzgoBe.domain.chat.chatMessage.controller;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.ChatMessageResponse;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessagePage;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rest-api/v1/chat-room/message")
@RequiredArgsConstructor
@Tag(name = "ChatMessage", description = "채팅 메시지 API")
public class ApiV1ChatMessageController {
    private final ChatMessageService chatMessageService;

    // 채팅방의 이전 메시지 가져오기 & 모든 메시지 읽음 처리[참여자 권한]
    @GetMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 과거 메시지 조회", description = "채팅방의 과거 메시지를 조회합니다.(기본설정: page=0, size=20)")
    public ApiResponse<List<ChatMessageResponse>> findByChatRoomId(@ModelAttribute ChatMessagePage request,
                                                             @PathVariable("chatRoomId") Long chatRoomId, @CurrentUser CurrentUserDto currentUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(chatMessageService.findByChatRoomId(chatRoomId, pageable, currentUser));
    }

    // 채팅방에서 메시지 검색(내용) [참여자 권한]
    @GetMapping("/{chatRoomId}/search")
    @Operation(summary = "채팅방 메시지 검색(내용)", description = "채팅방에서 메시지를 검색합니다.")
    public ApiResponse<List<ChatMessageResponse>> searchChatMessage(@ModelAttribute ChatMessagePage request, @PathVariable("chatRoomId") Long chatRoomId,
                                                              @RequestParam("keyword") String keyword, @CurrentUser CurrentUserDto currentUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(chatMessageService.searchByKeyword(chatRoomId, keyword, pageable, currentUser));
    }

    // 채팅방에서 이미지 메시지 생성 [참여자 권한]
    @PostMapping("/image/{chatRoomId}")
    @Operation(summary = "채팅방에 이미지 메시지 전송", description = "채팅방에 이미지 메시지를 전송합니다.")
    public ApiResponse<Void> writeImageMessage(@PathVariable("chatRoomId") Long chatRoomId,
                                                 @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFiles,
                                                 @CurrentUser CurrentUserDto currentUser) {
        chatMessageService.writeImageMessage(chatRoomId, imageFiles, currentUser);
        return ApiResponse.success();
    }

    // 메시지 삭제 [참여자 권한]
    @DeleteMapping("/{messageId}")
    @Operation(summary = "메시지 삭제", description = "메시지를 삭제합니다.")
    public ApiResponse<Void> deleteChatMessage(@PathVariable("messageId") Long messageId, @CurrentUser CurrentUserDto currentUser) {
        chatMessageService.deleteChatMessage(messageId, currentUser);
        return ApiResponse.success();
    }
}
