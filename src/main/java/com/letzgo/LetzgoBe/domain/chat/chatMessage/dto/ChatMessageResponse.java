package com.letzgo.LetzgoBe.domain.chat.chatMessage.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private String content;
    private List<String> imageUrls;
    private Long unreadCount;
    private LocalDateTime createdAt;
}
