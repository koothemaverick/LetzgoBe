package com.letzgo.LetzgoBe.domain.community.post.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.PostRequest;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.XYRequest;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.DetailPostResponse;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.PostResponse;
import com.letzgo.LetzgoBe.domain.community.post.entity.PostPage;
import com.letzgo.LetzgoBe.domain.community.post.service.PostService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value="/rest-api/v1/post")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    // 본인 & 팔로우한 유저의 게시글 조회
    @GetMapping("/main")
    public ApiResponse<List<DetailPostResponse>> getMainPost(@ModelAttribute PostPage request, @CurrentUser CurrentUserDto loginUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.getMainPost(loginUser, pageable));
    }

    // 사용자 위치 주변 게시글 조회
    @GetMapping("/surroundings")
    public ApiResponse<List<DetailPostResponse>> getSurroundings(@ModelAttribute PostPage request,
                                                           @RequestBody @Valid XYRequest xyRequest,
                                                           @CurrentUser CurrentUserDto loginUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.findPostsWithinRadius(xyRequest, pageable, loginUser));
    }

    // 해당 사용자가 작성한 게시글 조회
    @GetMapping("/member/{memberId}")
    public ApiResponse<List<DetailPostResponse>> getMemberPost(@ModelAttribute PostPage request,
                                                         @PathVariable("memberId") Long memberId,
                                                         @CurrentUser CurrentUserDto loginUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.findByMemberId(memberId, pageable, loginUser));
    }

    // 해당 게시글 상세 조회
    @GetMapping("/detail/{postId}")
    public ApiResponse<DetailPostResponse> getDetailPost(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto loginUser) {
        return ApiResponse.success(postService.findById(postId, loginUser));
    }

    // 해당 사용자가 저장한 게시글 조회
    @GetMapping("/collection/{memberId}")
    public ApiResponse<List<PostResponse>> getMemberCollectionPost(@ModelAttribute PostPage request,
                                                             @PathVariable("memberId") Long memberId) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.getSavedPostByMember(memberId, pageable));
    }

    // 사용자 닉네임 & 게시글 내용 검색
    @GetMapping("/search")
    public ApiResponse<List<PostResponse>> searchPost(@ModelAttribute PostPage request, @RequestParam("keyword") String keyword) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.searchByKeyword(keyword, pageable));
    }

    // 게시글 저장
    @PostMapping("/collection/{postId}")
    public ApiResponse<Void> addCollectionPost(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto loginUser) {
        postService.addCollectionPost(postId, loginUser);
        return ApiResponse.success();
    }

    // 게시글 저장 취소
    @DeleteMapping("/collection/{postId}")
    public ApiResponse<Void> deleteCollectionPost(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto loginUser) {
        postService.deleteCollectionPost(postId, loginUser);
        return ApiResponse.success();
    }

    // 게시글 좋아요
    @PostMapping("/like/{postId}")
    public ApiResponse<Void> addPostLike(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto loginUser) {
        postService.addPostLike(postId, loginUser);
        return ApiResponse.success();
    }

    // 게시글 좋아요 취소
    @DeleteMapping("/like/{postId}")
    public ApiResponse<Void> deletePostLike(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto loginUser) {
        postService.deletePostLike(postId, loginUser);
        return ApiResponse.success();
    }

    // 게시글 생성
    @PostMapping
    public ApiResponse<Void> addPost(@RequestPart(value = "postForm") @Valid PostRequest postRequest,
                                       @RequestPart(value = "imageFiles") List<MultipartFile> imageFiles,
                                       @CurrentUser CurrentUserDto loginUser) {
        postService.addPost(postRequest, imageFiles, loginUser);
        return ApiResponse.success();
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ApiResponse<Void> updatePost(@PathVariable("postId") Long postId,
                                              @RequestPart(value = "postForm") @Valid PostRequest postRequest,
                                              @RequestPart(value = "imageFile") List<MultipartFile> imageFiles,
                                          @CurrentUser CurrentUserDto loginUser) {
        postService.updatePost(postId, postRequest, imageFiles, loginUser);
        return ApiResponse.success();
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto loginUser) {
        postService.deletePost(postId, loginUser);
        return ApiResponse.success();
    }
}
