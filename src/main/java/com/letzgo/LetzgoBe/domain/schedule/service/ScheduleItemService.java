package com.letzgo.LetzgoBe.domain.schedule.service;

import com.letzgo.LetzgoBe.domain.schedule.dto.ScheduleMemoDto;
import com.letzgo.LetzgoBe.domain.schedule.dto.SchedulePlaceDto;
import com.letzgo.LetzgoBe.domain.schedule.entity.ItemType;
import com.letzgo.LetzgoBe.domain.schedule.entity.Schedule;
import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleMemo;
import com.letzgo.LetzgoBe.domain.schedule.entity.SchedulePlace;
import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleMemoRepository;
import com.letzgo.LetzgoBe.domain.schedule.repository.SchedulePlaceRepository;
import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 일정 항목(장소/메모)을 등록, 조회, 순서 조정하는 서비스
 */
@Service
@RequiredArgsConstructor
public class ScheduleItemService {

    private final ScheduleRepository scheduleRepository;
    private final SchedulePlaceRepository schedulePlaceRepository;
    private final ScheduleMemoRepository scheduleMemoRepository;

    @Transactional
    public Long addPlace(Long schedulePk, SchedulePlaceDto dto) {
        Schedule schedule = scheduleRepository.findById(schedulePk).orElseThrow();

        SchedulePlace place = new SchedulePlace();
        place.setSchedule(schedule);
        place.setName(dto.getName());
        place.setAddress(dto.getAddress());
        place.setPlaceId(dto.getPlaceId());
        place.setLatitude(dto.getLatitude());
        place.setLongitude(dto.getLongitude());
        place.setOrderIndex(dto.getOrderIndex());

        // sequence 계산 (orderIndex별로 가장 높은 값 + 1)
        int nextSeq = schedulePlaceRepository.findBySchedule_SchedulePk(schedulePk).stream()
                .filter(p -> p.getOrderIndex() == dto.getOrderIndex())
                .map(SchedulePlace::getOrderIndex)
                .max(Comparator.naturalOrder())
                .orElse(0);
        place.setOrderIndex(dto.getOrderIndex());

        return schedulePlaceRepository.save(place).getSchedulePlacePk();
    }

    @Transactional
    public Long addMemo(Long schedulePlacePk, ScheduleMemoDto dto) {
        SchedulePlace place = schedulePlaceRepository.findById(schedulePlacePk).orElseThrow();

        ScheduleMemo memo = new ScheduleMemo();
        memo.setSchedulePlace(place);
        memo.setContent(dto.getContent());

        return scheduleMemoRepository.save(memo).getScheduleMemoPk();
    }

    @Transactional
    public void deletePlace(Long schedulePlacePk) {
        schedulePlaceRepository.deleteById(schedulePlacePk);
    }

    @Transactional
    public void deleteMemo(Long schedulePlacePk) {
        ScheduleMemo memo = scheduleMemoRepository.findBySchedulePlace_SchedulePlacePk(schedulePlacePk)
                .orElseThrow();
        scheduleMemoRepository.delete(memo);
    }

    public List<SchedulePlaceDto> getPlaces(Long schedulePk) {
        return schedulePlaceRepository.findBySchedule_SchedulePk(schedulePk).stream()
                .map(place -> {
                    SchedulePlaceDto dto = new SchedulePlaceDto();
                    dto.setSchedulePlacePk(place.getSchedulePlacePk());
                    dto.setName(place.getName());
                    dto.setAddress(place.getAddress());
                    dto.setPlaceId(place.getPlaceId());
                    dto.setLatitude(place.getLatitude());
                    dto.setLongitude(place.getLongitude());
                    dto.setOrderIndex(place.getOrderIndex());

                    scheduleMemoRepository.findBySchedulePlace_SchedulePlacePk(place.getSchedulePlacePk())
                            .ifPresent(memo -> {
                                dto.setMemoPk(memo.getScheduleMemoPk());
                                dto.setMemoContent(memo.getContent());
                            });
                    return dto;
                })
                .sorted(Comparator.comparingInt(SchedulePlaceDto::getOrderIndex))
                .collect(Collectors.toList());
    }
}

