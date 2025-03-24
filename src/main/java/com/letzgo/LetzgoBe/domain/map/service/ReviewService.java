package com.letzgo.LetzgoBe.domain.map.service;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.map.dto.ReviewDto;
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

    public void createReview(LoginUserDto loginUserDto, String placeId, ReviewDto reviewDto, MultipartFile image) {

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
            Review review = Review.builder()
                    .member(loginUserDto.ConvertToMember())
                    .place(place)
                    .content(reviewDto.getContent())
                    .rating(reviewDto.getRating())
                    .title(reviewDto.getTitle())
                    .photo(uploadedPhoto)
                    .build();

            reviewRepository.save(review);
        }
    }

    @Transactional
    public void updateReview(LoginUserDto loginUserDto, ReviewDto reviewDto, MultipartFile image) {
        if (loginUserDto.getName().equals(reviewDto.getAccount())) {
            Review review = reviewRepository.findById(reviewDto.getId()).orElseThrow(()->new NoSuchElementException());
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
                            reviewDto.getTitle(),
                            reviewDto.getContent(),
                            reviewDto.getRating());

                } catch (IOException e) {
                    throw new ServiceException(ReturnCode.INTERNAL_ERROR);
                }

                review.update(
                        reviewDto.getTitle(),
                        reviewDto.getContent(),
                        reviewDto.getRating());
            }


        }
    }


    public void deleteReview(LoginUserDto loginUserDto, ReviewDto reviewDto) {
        if (loginUserDto.getName().equals(reviewDto.getAccount())) {
            reviewRepository.deleteById(reviewDto.getId());
        }
    }
}
