package com.letzgo.LetzgoBe.domain.schedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleMemberDto {
    private Long memberPk;
    private String nickname;
    private String profileImageUrl;
}
