package com.letzgo.LetzgoBe.domain.schedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScheduleDto {
    private Long schedulePk;
    private Long hostAccountPk;
    private Long regionPk;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
}