package com.letzgo.LetzgoBe.domain.map.service;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.ReviewDto;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.entity.Review;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    public boolean createReview(LoginUserDto loginUserDto, String placeId, ReviewDto reviewDto) {
        Place place = placeRepository.findByPlaceId(placeId);

        Review review = Review.builder()
                .member(loginUserDto.ConvertToMember())
                .place(place)
                .content(reviewDto.getContent())
                .rating(reviewDto.getRating())
                .photo_dir(reviewDto.getPhoto_dir())
                .build();

        Review savedReview = reviewRepository.save(review);

        if (savedReview != null) {
            return true;
        }
        return false;
    }
    public boolean deleteReview(LoginUserDto loginUserDto, ReviewDto reviewDto) {
        if (loginUserDto.getName() == reviewDto.getAccount()) {

        }
        return true;
    }

}
