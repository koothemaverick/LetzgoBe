package com.letzgo.LetzgoBe.domain.map.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.account.member.mapper.MemberMapper;
import com.letzgo.LetzgoBe.domain.map.dto.req.ReviewRequest;
import com.letzgo.LetzgoBe.domain.map.entity.Photo;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.entity.Review;
import com.letzgo.LetzgoBe.domain.map.repository.PhotoRepository;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.repository.ReviewRepository;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import com.letzgo.LetzgoBe.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final PhotoRepository photoRepository;
    private final S3Service s3Service;
    private final MemberMapper memberMapper;

    @Transactional
    public void createReview(CurrentUserDto currentUserDto, String placeId, ReviewRequest ReviewRequest, MultipartFile image) {

        Place place = placeRepository.findByPlaceId(placeId);
        Photo uploadedPhoto = null;

        if(image != null) {
            try {
                String storeDir = s3Service.uploadFile(image, "review");

                uploadedPhoto = Photo.builder()
                        .upload_name(image.getOriginalFilename())
                        .store_dir(storeDir)
                        .build();

                photoRepository.save(uploadedPhoto);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Review review = Review.builder()
                .member(memberMapper.toMember(currentUserDto))
                .place(place)
                .content(ReviewRequest.getContent())
                .rating(ReviewRequest.getRating())
                .title(ReviewRequest.getTitle())
                .photo(uploadedPhoto)
                .build();

        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(CurrentUserDto currentUserDto, Long reviewId, ReviewRequest reviewRequest, MultipartFile image) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(()->new NoSuchElementException());

        if(currentUserDto.getName().equals(review.getMember().getName()))
            if(image!= null) {
                //기존이미지 삭제
                s3Service.deleteFile(review.getPhoto().getStore_dir());
                //새 이미지 저장
                try {
                    String storeDir = s3Service.uploadFile(image, "review");

                    Photo uploadedPhoto = Photo.builder()
                            .upload_name(image.getOriginalFilename())
                            .store_dir(storeDir)
                            .build();

                    photoRepository.save(uploadedPhoto);

                    review.update(
                            uploadedPhoto,
                            reviewRequest.getTitle(),
                            reviewRequest.getContent(),
                            reviewRequest.getRating());

                } catch (IOException e) {
                    throw new ServiceException(ReturnCode.INTERNAL_ERROR);
                }
            }
                review.update(
                        reviewRequest.getTitle(),
                        reviewRequest.getContent(),
                        reviewRequest.getRating());

    }

    @Transactional
    public void deleteReview(CurrentUserDto currentUserDto, Long reviewId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Review review = optionalReview.orElseThrow(()->new NoSuchElementException());
        if (review.getMember().getName()
                .equals(currentUserDto.getName())) {
            if (review.getPhoto() != null)
                s3Service.deleteFile(review.getPhoto().getStore_dir());
            reviewRepository.deleteById(reviewId);
        }
    }
}
