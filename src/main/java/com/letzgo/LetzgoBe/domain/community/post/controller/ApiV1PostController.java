package com.letzgo.LetzgoBe.domain.community.post.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.PostRequest;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.XYRequest;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.DetailPostResponse;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.PostResponse;
import com.letzgo.LetzgoBe.domain.community.post.entity.PostPage;
import com.letzgo.LetzgoBe.domain.community.post.service.PostService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.common.response.LetzgoPage;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
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
    public ApiResponse<DetailPostResponse> getMainPost(@ModelAttribute PostPage request, @LoginUser LoginUserDto loginUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(LetzgoPage.of(postService.getMainPost(loginUser, pageable)));
    }

    // 사용자 위치 주변 게시글 조회
    @GetMapping("/surroundings")
    public ApiResponse<DetailPostResponse> getSurroundings(@ModelAttribute PostPage request,
                                                           @RequestBody @Valid XYRequest xyRequest,
                                                           @LoginUser LoginUserDto loginUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(LetzgoPage.of(postService.findPostsWithinRadius(xyRequest, pageable, loginUser)));
    }

    // 해당 사용자가 작성한 게시글 조회
    @GetMapping("/member/{memberId}")
    public ApiResponse<DetailPostResponse> getMemberPost(@ModelAttribute PostPage request,
                                                         @PathVariable("memberId") Long memberId,
                                                         @LoginUser LoginUserDto loginUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(LetzgoPage.of(postService.findByMemberId(memberId, pageable, loginUser)));
    }

    // 해당 게시글 상세 조회
    @GetMapping("/detail/{postId}")
    public ApiResponse<DetailPostResponse> getDetailPost(@PathVariable("postId") Long postId, @LoginUser LoginUserDto loginUser) {
        return ApiResponse.of(postService.findById(postId, loginUser));
    }

    // 해당 사용자가 저장한 게시글 조회
    @GetMapping("/collection/{memberId}")
    public ApiResponse<PostResponse> getMemberCollectionPost(@ModelAttribute PostPage request,
                                                             @PathVariable("memberId") Long memberId) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(LetzgoPage.of(postService.getSavedPostByMember(memberId, pageable)));
    }

    // 사용자 닉네임 & 게시글 내용 검색
    @GetMapping("/search")
    public ApiResponse<PostResponse> searchPost(@ModelAttribute PostPage request, @RequestParam("keyword") String keyword) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(LetzgoPage.of(postService.searchByKeyword(keyword, pageable)));
    }

    // 게시글 저장
    @PostMapping("/collection/{postId}")
    public ApiResponse<String> addCollectionPost(@PathVariable("postId") Long postId, @LoginUser LoginUserDto loginUser) {
        postService.addCollectionPost(postId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 게시글 저장 취소
    @DeleteMapping("/collection/{postId}")
    public ApiResponse<String> deleteCollectionPost(@PathVariable("postId") Long postId, @LoginUser LoginUserDto loginUser) {
        postService.deleteCollectionPost(postId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 게시글 좋아요
    @PostMapping("/like/{postId}")
    public ApiResponse<String> addPostLike(@PathVariable("postId") Long postId, @LoginUser LoginUserDto loginUser) {
        postService.addPostLike(postId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 게시글 좋아요 취소
    @DeleteMapping("/like/{postId}")
    public ApiResponse<String> deletePostLike(@PathVariable("postId") Long postId, @LoginUser LoginUserDto loginUser) {
        postService.deletePostLike(postId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 게시글 생성
    @PostMapping
    public ApiResponse<String> addPost(@RequestPart(value = "postForm") @Valid PostRequest postRequest,
                                       @RequestPart(value = "imageFiles") List<MultipartFile> imageFiles,
                                       @LoginUser LoginUserDto loginUser) {
        postService.addPost(postRequest, imageFiles, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ApiResponse<String> updatePost(@PathVariable("postId") Long postId,
                                              @RequestPart(value = "postForm") @Valid PostRequest postRequest,
                                              @RequestPart(value = "imageFile") List<MultipartFile> imageFiles,
                                          @LoginUser LoginUserDto loginUser) {
        postService.updatePost(postId, postRequest, imageFiles, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ApiResponse<String> deletePost(@PathVariable("postId") Long postId, @LoginUser LoginUserDto loginUser) {
        postService.deletePost(postId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }
}
