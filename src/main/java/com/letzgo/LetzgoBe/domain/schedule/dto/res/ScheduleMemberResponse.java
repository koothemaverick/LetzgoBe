package com.letzgo.LetzgoBe.domain.schedule.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleMemberResponse {
    private Long memberPk;
    private String nickname;
    private String profileImageUrl;
}
