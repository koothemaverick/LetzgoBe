//package com.letzgo.LetzgoBe.domain.schedule.service;
//
//import com.letzgo.LetzgoBe.domain.map.entity.Place;
//import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
//import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleItem;
//import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleItemRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 장소 간 최적 경로 계산 및 순서 재정렬을 위한 서비스 클래스
// */
//@Service
//@RequiredArgsConstructor
//public class RouteService {
//
//    private final ScheduleItemRepository itemRepository;
//    private final PlaceRepository placeRepository;
//    private final GoogleDirectionsClient directionsClient;
//
//    /**
//     * 일정 내 장소들의 최적 방문 순서를 계산하고 DB에 반영
//     */
//    @Transactional
//    public void optimizeSchedulePlaces(Long schedulePk) {
//        // 1. 일정 내 장소 항목들 조회
//        List<ScheduleItem> placeItems = itemRepository
//                .findBySchedule_SchedulePkAndItemType_TypeName(schedulePk, "장소");
//
//        // 2. 장소 ID → Place 객체 매핑
//        List<Place> places = placeItems.stream()
//                .map(item -> placeRepository.findById(item.getItemPk()).orElseThrow())
//                .collect(Collectors.toList());
//
//        // 3. 좌표 기반 최적 경로 요청
//        List<Integer> optimalOrder = directionsClient.optimizeWaypointOrder(places);
//
//        // 4. 순서 재정렬
//        for (int i = 0; i < optimalOrder.size(); i++) {
//            int newIndex = i;
//            int oldIndex = optimalOrder.get(i);
//            placeItems.get(oldIndex).setSequence(newIndex);
//        }
//        itemRepository.saveAll(placeItems);
//    }
//}