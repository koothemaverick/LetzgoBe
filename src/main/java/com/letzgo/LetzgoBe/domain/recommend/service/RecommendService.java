package com.letzgo.LetzgoBe.domain.recommend.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.res.PlaceResponse;
import com.letzgo.LetzgoBe.domain.map.entity.PlacePage;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.service.MapApiService;
import com.letzgo.LetzgoBe.global.common.response.PageResponse;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final DataSource dataSource;
    private final PlaceRepository placeRepository;
    private final MapApiService mapApiService;

    // page&size만큼의 추천장소 반환, 아이템 기반 추천 필터링
    @Transactional(readOnly = true)
    public PageResponse<PlaceResponse> getRecommendedPlace(CurrentUserDto currentUserDto, Pageable pageable) {
        checkPageSize(pageable.getPageSize());
        List<Long> places = new ArrayList<>(); // place_pk 리스트

        try {
            DataModel model = new PostgreSQLJDBCDataModel(dataSource, "Review", "account_pk", "place_pk", "rating", null);

            ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(model);
            ItemBasedRecommender recommender = new GenericItemBasedRecommender(model, itemSimilarity);

            // num은 페이지 사이즈만큼 추천받도록 수정
            int num = pageable.getPageSize();
            List<RecommendedItem> recommendations = recommender.recommend(currentUserDto.getId(), num);
            for (RecommendedItem item : recommendations) {
                places.add(item.getItemID());
            }

        } catch (TasteException e) {
            e.printStackTrace();
        }

        // place_pk 리스트 → placeDto 리스트 변환
        List<PlaceResponse> placeResponses = places.stream()
                .map(place -> {
                    try {
                        return mapApiService.getPlaceDetails(
                                placeRepository.findById(place)
                                        .orElseThrow(NoSuchElementException::new)
                                        .getPlaceId());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // 페이지로 감싸서 반환 (PageImpl 사용)
        return PageResponse.of(new PageImpl<>(placeResponses, pageable, placeResponses.size()));
    }

    // 요청 페이지 수 제한
    public void checkPageSize(int pageSize) {
        int maxPageSize = PlacePage.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new ServiceException(ReturnCode.PAGE_REQUEST_FAIL);
        }
    }
}
