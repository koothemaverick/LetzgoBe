package com.letzgo.LetzgoBe.domain.schedule.service;

import com.letzgo.LetzgoBe.domain.schedule.dto.ScheduleItemDto;
import com.letzgo.LetzgoBe.domain.schedule.entity.ItemType;
import com.letzgo.LetzgoBe.domain.schedule.entity.Schedule;
import com.letzgo.LetzgoBe.domain.schedule.entity.ScheduleItem;
import com.letzgo.LetzgoBe.domain.schedule.repository.ItemTypeRepository;
import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleItemRepository;
import com.letzgo.LetzgoBe.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 일정 항목(장소/메모)을 등록, 조회, 순서 조정하는 서비스
 */
@Service
@RequiredArgsConstructor
public class ScheduleItemService {

    private final ScheduleItemRepository scheduleItemRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final ScheduleRepository scheduleRepository;

    /** 일정에 장소/메모 항목 추가 */
    @Transactional
    public ScheduleItem addItem(Long schedulePk, Long itemPk, String itemTypeName, int orderIndex) {
        Schedule schedule = scheduleRepository.findById(schedulePk).orElseThrow();
        ItemType itemType = itemTypeRepository.findByTypeName(itemTypeName).orElseThrow();

        ScheduleItem item = new ScheduleItem();
        item.setSchedule(schedule);
        item.setItemType(itemType);
        item.setItemPk(itemPk);
        item.setOrderIndex(orderIndex);
        item.setSequence(orderIndex); // 초기에는 순서 동일하게 설정
        return scheduleItemRepository.save(item);
    }

    /** 특정 일정의 장소 or 메모 목록 가져오기 */
    public List<ScheduleItem> getItemsByType(Long schedulePk, String typeName) {
        return scheduleItemRepository.findBySchedule_SchedulePkAndItemType_TypeName(schedulePk, typeName);
    }

    /** 항목 순서 업데이트 */
    @Transactional
    public void updateItemSequences(List<ScheduleItem> items) {
        for (int i = 0; i < items.size(); i++) {
            ScheduleItem item = items.get(i);
            item.setSequence(i);
        }
        scheduleItemRepository.saveAll(items);
    }

    public List<ScheduleItemDto> getItemDtosBySchedule(Long schedulePk) {
        List<ScheduleItem> items = scheduleItemRepository.findBySchedule_SchedulePk(schedulePk);
        return items.stream().map(item -> {
            ScheduleItemDto dto = new ScheduleItemDto();
            dto.setScheduleItemPk(item.getScheduleItemPk());
            dto.setSchedulePk(schedulePk);
            dto.setItemPk(item.getItemPk());
            dto.setItemTypeName(item.getItemType().getTypeName());
            dto.setOrderIndex(item.getOrderIndex());
            dto.setSequence(item.getSequence());
            return dto;
        }).toList();
    }

}
