package com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 직렬화에서 제외
public class ChatRoomForm {
    private String title;

    @Builder.Default
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();
}
