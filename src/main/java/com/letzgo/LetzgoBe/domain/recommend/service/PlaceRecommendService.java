package com.letzgo.LetzgoBe.domain.recommend.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.PlaceDto;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceRecommendService {
    private final DataSource dataSource;
    private final PlaceRepository placeRepository;
    //num개수만큼의 추천장소 반환, 사용자기반 추천필터링
    public List<PlaceDto> getRecommededPlace(LoginUserDto loginUserDto, int num) {

        List<Long> places = new ArrayList<>(); //pk List

        try {
            DataModel model = new MySQLJDBCDataModel(dataSource, "Review", "User", "Place", "Rating", null);

            UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, userSimilarity, model); //0.1보다 유사도 큰 장소 추천
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, userSimilarity);

            List<RecommendedItem> recommendations = recommender.recommend(loginUserDto.getId(), num);//num개 추천
            for (RecommendedItem item : recommendations) {
                places.add(item.getItemID());
            }

        } catch (TasteException e) {
            e.printStackTrace();
        }
        return places.stream()
                .map(place -> PlaceDto.entityToDto(placeRepository.findById(place))) //place pk List -> place dto List
                .collect(Collectors.toList());
    }
}
