package com.letzgo.LetzgoBe.domain.schedule.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleMemoRequest {
    private Long scheduleMemoPk;
    private String content;
}
