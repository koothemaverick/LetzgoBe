package com.letzgo.LetzgoBe.domain.schedule.service;

import com.letzgo.LetzgoBe.domain.schedule.dto.req.ScheduleMemoRequest;
import com.letzgo.LetzgoBe.domain.schedule.dto.SchedulePlaceDto;
import com.letzgo.LetzgoBe.domain.schedule.entity.Schedule;
import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleMemo;
import com.letzgo.LetzgoBe.domain.schedule.entity.SchedulePlace;
import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleMemoRepository;
import com.letzgo.LetzgoBe.domain.schedule.repository.SchedulePlaceRepository;
import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

        int nextSeq = schedulePlaceRepository.findBySchedule_SchedulePk(schedulePk).stream()
                .filter(p -> p.getOrderIndex() == dto.getOrderIndex())
                .map(SchedulePlace::getSequence)
                .max(Comparator.naturalOrder())
                .orElse(0) + 10;

        place.setSequence(nextSeq);

        return schedulePlaceRepository.save(place).getSchedulePlacePk();
    }

    @Transactional
    public Long addMemo(Long schedulePlacePk, ScheduleMemoRequest dto) {
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
                    dto.setSequence(place.getSequence());

                    scheduleMemoRepository.findBySchedulePlace_SchedulePlacePk(place.getSchedulePlacePk())
                            .ifPresent(memo -> {
                                dto.setMemoPk(memo.getScheduleMemoPk());
                                dto.setMemoContent(memo.getContent());
                            });

                    return dto;
                })
                .sorted(Comparator.comparingInt(SchedulePlaceDto::getOrderIndex)
                        .thenComparingInt(SchedulePlaceDto::getSequence))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SchedulePlaceDto> getOptimalRoute(Long schedulePk, int orderIndex) {
        List<SchedulePlace> originalPlaces = schedulePlaceRepository
                .findBySchedule_SchedulePkAndOrderIndexOrderBySequence(schedulePk, orderIndex);

        if (originalPlaces == null || originalPlaces.size() <= 2) {
            return originalPlaces.stream()
                    .map(place -> {
                        SchedulePlaceDto dto = new SchedulePlaceDto();
                        dto.setSchedulePlacePk(place.getSchedulePlacePk());
                        dto.setName(place.getName());
                        dto.setAddress(place.getAddress());
                        dto.setPlaceId(place.getPlaceId());
                        dto.setLatitude(place.getLatitude());
                        dto.setLongitude(place.getLongitude());
                        dto.setOrderIndex(place.getOrderIndex());
                        dto.setSequence(place.getSequence());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        List<SchedulePlace> optimizedRoute = solveTspWith2Opt(originalPlaces);

        System.out.println("== 최적 경로 순서 ==");
        optimizedRoute.forEach(p -> System.out.println(p.getName()));

        return optimizedRoute.stream()
                .map(place -> {
                    SchedulePlaceDto dto = new SchedulePlaceDto();
                    dto.setSchedulePlacePk(place.getSchedulePlacePk());
                    dto.setName(place.getName());
                    dto.setAddress(place.getAddress());
                    dto.setPlaceId(place.getPlaceId());
                    dto.setLatitude(place.getLatitude());
                    dto.setLongitude(place.getLongitude());
                    dto.setOrderIndex(place.getOrderIndex());
                    dto.setSequence(place.getSequence());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void reorderPlaces(Long schedulePk, int orderIndex, List<Long> reorderedPlacePks) {
        List<SchedulePlace> places = schedulePlaceRepository.findBySchedule_SchedulePkAndOrderIndexOrderBySequence(schedulePk, orderIndex);
        Map<Long, SchedulePlace> placeMap = places.stream().collect(Collectors.toMap(SchedulePlace::getSchedulePlacePk, p -> p));

        List<SchedulePlace> updated = new ArrayList<>();
        for (int i = 0; i < reorderedPlacePks.size(); i++) {
            Long pk = reorderedPlacePks.get(i);
            SchedulePlace place = placeMap.get(pk);
            if (place != null) {
                place.setSequence(i + 1);
                updated.add(place);
            }
        }
        schedulePlaceRepository.saveAll(updated);
    }

    private List<SchedulePlace> solveTspWith2Opt(List<SchedulePlace> places) {
        List<SchedulePlace> route = solveTspNearestNeighbor(places);

        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 1; i < route.size() - 2; i++) {
                for (int j = i + 1; j < route.size() - 1; j++) {
                    double currentDist = getDistance(route.get(i - 1), route.get(i)) +
                            getDistance(route.get(j), route.get(j + 1));
                    double newDist = getDistance(route.get(i - 1), route.get(j)) +
                            getDistance(route.get(i), route.get(j + 1));
                    if (newDist < currentDist) {
                        Collections.reverse(route.subList(i, j + 1));
                        improved = true;
                    }
                }
            }
        }

        return route;
    }

    private List<SchedulePlace> solveTspNearestNeighbor(List<SchedulePlace> places) {
        List<SchedulePlace> unvisited = new ArrayList<>(places);
        List<SchedulePlace> route = new ArrayList<>();

        SchedulePlace current = unvisited.remove(0);
        route.add(current);

        while (!unvisited.isEmpty()) {
            final SchedulePlace currentFinal = current;
            SchedulePlace next = Collections.min(unvisited,
                    Comparator.comparingDouble(p -> getDistance(currentFinal, p)));
            route.add(next);
            unvisited.remove(next);
            current = next;
        }

        return route;
    }

    private double getDistance(SchedulePlace a, SchedulePlace b) {
        if (a.getLatitude() == null || b.getLatitude() == null ||
                a.getLongitude() == null || b.getLongitude() == null) {
            return Double.MAX_VALUE;
        }

        double lat1 = Math.toRadians(a.getLatitude());
        double lon1 = Math.toRadians(a.getLongitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double lon2 = Math.toRadians(b.getLongitude());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double haversine = Math.pow(Math.sin(dlat / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow(Math.sin(dlon / 2), 2);

        double R = 6371.0;
        return 2 * R * Math.asin(Math.sqrt(haversine));
    }
}
