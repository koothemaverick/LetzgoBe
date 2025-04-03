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
    @Value("${schedule.use-page}")
    //지역당 가져올 페이지 수, 페이지당 숙소 20개
    private int page;

    @Scheduled(cron = "${schedule.cron}")
    public void mainJob() {
        try {
            if (useSchedule)
                hotelInfoService.getHotelsInfo(page);
        } catch (Exception e) {
            log.warn("hotelInfoService 스케줄링 중 오류 발생");
        }
    }
}
