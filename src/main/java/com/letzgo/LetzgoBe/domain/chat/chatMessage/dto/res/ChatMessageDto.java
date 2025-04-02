package com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ChatMessageDto {
    private Long id;
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private String content;
    private List<String> imageUrls;
    private Long readCount;
    private LocalDateTime createdAt;
}
