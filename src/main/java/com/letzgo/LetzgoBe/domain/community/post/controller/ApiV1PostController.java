package com.letzgo.LetzgoBe.domain.community.post.controller;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.PostRequest;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.XYRequest;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.DetailPostResponse;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.PostResponse;
import com.letzgo.LetzgoBe.domain.community.post.entity.PostPage;
import com.letzgo.LetzgoBe.domain.community.post.service.PostService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Post", description = "게시글 API")
public class ApiV1PostController {
    private final PostService postService;

    // 본인 & 팔로우한 유저의 게시글 조회
    @GetMapping("/main")
    @Operation(summary = "게시글 조회", description = "게시글을 조회합니다.")
    public ApiResponse<List<DetailPostResponse>> getMainPost(@ModelAttribute PostPage request, @CurrentUser CurrentUserDto currentUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.getMainPost(currentUser, pageable));
    }

    // 사용자 위치 주변 게시글 조회
    @GetMapping("/surroundings")
    @Operation(summary = "사용자 위치 주변 게시글 조회", description = "사용자 위치 주변 게시글을 조회합니다.")
    public ApiResponse<List<DetailPostResponse>> getSurroundings(@ModelAttribute PostPage request,
                                                           @RequestBody @Valid XYRequest xyRequest,
                                                           @CurrentUser CurrentUserDto currentUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.findPostsWithinRadius(xyRequest, pageable, currentUser));
    }

    // 특정 사용자가 작성한 게시글 조회
    @GetMapping("/member/{memberId}")
    @Operation(summary = "특정 사용자의 게시글 조회", description = "특정 사용자의 게시글을 조회합니다.")
    public ApiResponse<List<DetailPostResponse>> getMemberPost(@ModelAttribute PostPage request,
                                                         @PathVariable("memberId") Long memberId,
                                                         @CurrentUser CurrentUserDto currentUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.findByMemberId(memberId, pageable, currentUser));
    }

    // 해당 게시글 상세 조회
    @GetMapping("/detail/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글을 상세 조회합니다.")
    public ApiResponse<DetailPostResponse> getDetailPost(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto currentUser) {
        return ApiResponse.success(postService.findById(postId, currentUser));
    }

    // 해당 사용자가 저장한 게시글 조회
    @GetMapping("/collection/{memberId}")
    @Operation(summary = "저장한 게시글 조회", description = "저장한 게시글을 조회합니다.")
    public ApiResponse<List<PostResponse>> getMemberCollectionPost(@ModelAttribute PostPage request,
                                                             @PathVariable("memberId") Long memberId) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.getSavedPostByMember(memberId, pageable));
    }

    // 사용자 닉네임 & 게시글 내용 검색
    @GetMapping("/search")
    @Operation(summary = "사용자 닉네임 & 게시글 내용 검색", description = "사용자 닉네임 & 게시글 내용을 검색합니다.")
    public ApiResponse<List<PostResponse>> searchPost(@ModelAttribute PostPage request, @RequestParam("keyword") String keyword) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(postService.searchByKeyword(keyword, pageable));
    }

    // 게시글 저장
    @PostMapping("/collection/{postId}")
    @Operation(summary = "게시글 저장", description = "게시글을 저장합니다.")
    public ApiResponse<Void> addCollectionPost(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto currentUser) {
        postService.addCollectionPost(postId, currentUser);
        return ApiResponse.success();
    }

    // 게시글 저장 취소
    @DeleteMapping("/collection/{postId}")
    @Operation(summary = "게시글 저장 취소", description = "게시글 저장을 취소합니다.")
    public ApiResponse<Void> deleteCollectionPost(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto currentUser) {
        postService.deleteCollectionPost(postId, currentUser);
        return ApiResponse.success();
    }

    // 게시글 좋아요
    @PostMapping("/like/{postId}")
    @Operation(summary = "게시글 좋아요", description = "게시글을 좋아합니다.")
    public ApiResponse<Void> addPostLike(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto currentUser) {
        postService.addPostLike(postId, currentUser);
        return ApiResponse.success();
    }

    // 게시글 좋아요 취소
    @DeleteMapping("/like/{postId}")
    @Operation(summary = "게시글 좋아요 취소", description = "게시글 좋아요를 취소합니다.")
    public ApiResponse<Void> deletePostLike(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto currentUser) {
        postService.deletePostLike(postId, currentUser);
        return ApiResponse.success();
    }

    // 게시글 생성
    @PostMapping
    @Operation(summary = "게시글 생성", description = "게시글을 생성합니다.")
    public ApiResponse<Void> addPost(@RequestPart(value = "postForm") @Valid PostRequest postRequest,
                                       @RequestPart(value = "imageFiles") List<MultipartFile> imageFiles,
                                       @CurrentUser CurrentUserDto currentUser) {
        postService.addPost(postRequest, imageFiles, currentUser);
        return ApiResponse.success();
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    public ApiResponse<Void> updatePost(@PathVariable("postId") Long postId,
                                              @RequestPart(value = "postForm") @Valid PostRequest postRequest,
                                              @RequestPart(value = "imageFile") List<MultipartFile> imageFiles,
                                          @CurrentUser CurrentUserDto currentUser) {
        postService.updatePost(postId, postRequest, imageFiles, currentUser);
        return ApiResponse.success();
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ApiResponse<Void> deletePost(@PathVariable("postId") Long postId, @CurrentUser CurrentUserDto currentUser) {
        postService.deletePost(postId, currentUser);
        return ApiResponse.success();
    }
}
