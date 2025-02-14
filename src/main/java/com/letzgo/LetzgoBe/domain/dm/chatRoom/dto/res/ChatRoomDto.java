package com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.res;

import com.letzgo.LetzgoBe.domain.dm.chatRoom.converter.StringDoubleListConverter;
import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomDto extends BaseEntity {
    @Convert(converter = StringDoubleListConverter.class)
    private List<List<String>> joinMemberIdNickNameList;
    private Long roomMemberLimit;
}
