package com.letzgo.LetzgoBe.domain.chat.chatMessage.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.req.ChatMessageForm;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.res.ChatMessageDto;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.ChatMessagePage;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.common.response.Page;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rest-api/v1/chat-room/message")
@RequiredArgsConstructor
public class ApiV1ChatMessageController {
    private final ChatMessageService chatMessageService;

    // 해당 채팅방의 메시지 실시간 조회 시작 [참여자 권한]
    @GetMapping("/{chatRoomId}")
    public ApiResponse<ChatMessageDto> findByChatRoomId(@ModelAttribute ChatMessagePage request,
                                                        @PathVariable("chatRoomId") Long chatRoomId, @LoginUser LoginUserDto loginUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(Page.of(chatMessageService.findByChatRoomId(chatRoomId, pageable, loginUser)));
    }

    // 해당 채팅방의 메시지 실시간 조회 중단 [참여자 권한]
    @PutMapping("/{chatRoomId}")
    public ApiResponse<String> updateLastReadMessage(@PathVariable("chatRoomId") Long chatRoomId, @LoginUser LoginUserDto loginUser){
        chatMessageService.updateLastReadMessage(chatRoomId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 해당 채팅방에서 메시지 검색(내용) [참여자 권한]
    @GetMapping("/{chatRoomId}/search")
    public ApiResponse<ChatMessageDto> searchChatMessage(@ModelAttribute ChatMessagePage request, @PathVariable("chatRoomId") Long chatRoomId,
                                                         @RequestParam("keyword") String keyword, @LoginUser LoginUserDto loginUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(Page.of(chatMessageService.searchByKeyword(chatRoomId, keyword, pageable, loginUser)));
    }

    // 해당 채팅방에서 메시지 생성 [참여자 권한]
    @MessageMapping("/{chatRoomId}") // stomp websocket 사용
    public ApiResponse<String> writeChatMessage(@DestinationVariable @PathVariable("chatRoomId") Long chatRoomId,
                                                @Valid ChatMessageForm chatMessageForm,
                                                @LoginUser LoginUserDto loginUser) {
        chatMessageService.writeChatMessage(chatRoomId, chatMessageForm, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 해당 채팅방에서 이미지 메시지 생성 [참여자 권한]
    @PostMapping("/image/{chatRoomId}")
    public ApiResponse<ChatMessageDto> writeImageMessage(@PathVariable("chatRoomId") Long chatRoomId,
                                                 @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFiles,
                                                 @LoginUser LoginUserDto loginUser) {
        return ApiResponse.of(chatMessageService.writeImageMessage(chatRoomId, imageFiles, loginUser));
    }

    // 메시지 삭제 [참여자 권한]
    @DeleteMapping("/{messageId}")
    public ApiResponse<String> deleteChatMessage(@PathVariable("messageId") Long messageId, @LoginUser LoginUserDto loginUser) {
        chatMessageService.deleteChatMessage(messageId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }
}
