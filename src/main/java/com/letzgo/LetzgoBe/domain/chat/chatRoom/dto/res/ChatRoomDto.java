package com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res;

import com.letzgo.LetzgoBe.domain.account.member.dto.res.SimpleMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {
    private Long id;
    private Long memberId;
    private String title;
    private List<SimpleMember> chatRoomMembers;
    private String lastMessage;
    private int memberCount;
}
