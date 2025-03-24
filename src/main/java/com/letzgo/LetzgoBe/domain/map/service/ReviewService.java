package com.letzgo.LetzgoBe.domain.map.service;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.ReviewDto;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.entity.Review;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    public boolean createReview(LoginUserDto loginUserDto, String placeId, ReviewDto reviewDto) {
        Place place = placeRepository.findByPlaceId(placeId);

        Review review = Review.builder()
                .user(loginUserDto.toEntity())
                .place(place)
                .content(reviewDto.getContent())
                .rating(reviewDto.getRating())
                .photoDir(reviewDto.getPhotoDir())
                .build();

        Review savedReview = reviewRepository.save(review);

        if (savedReview != null) {
            return true;
        }
        return false;
    }

    public void updateReview(LoginUserDto loginUserDto, ReviewDto reviewDto) {
        if (loginUserDto.getName() == reviewDto.getAccount()) {
            Optional<Review> review = reviewRepository.findById(reviewDto.getId());
            review.orElseThrow(()->new NoSuchElementException())
                    .update(reviewDto.getTitle(),
                            reviewDto.getPhotoDir(),
                            reviewDto.getContent(),
                            reviewDto.getRating());
        }
    }


    public void deleteReview(LoginUserDto loginUserDto, ReviewDto reviewDto) {
        if (loginUserDto.getName() == reviewDto.getAccount()) {
            reviewRepository.deleteById(reviewDto.getId());
        }
    }
}
