package com.letzgo.LetzgoBe.domain.hotel.scheduler;

import com.letzgo.LetzgoBe.domain.hotel.service.HotelInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotelInfoSchedule {
    private final HotelInfoService hotelInfoService;

    @Value("${schedule.use}")
    private boolean useSchedule;

    @Scheduled(cron = "${schedule.cron}")
    public void mainJob() {
        try {
            if (useSchedule)
                hotelInfoService.getHotelsInfo(2);
        } catch (Exception e) {
            log.warn("hotelInfoService 스케줄링 중 오류 발생");
        }
    }
}
