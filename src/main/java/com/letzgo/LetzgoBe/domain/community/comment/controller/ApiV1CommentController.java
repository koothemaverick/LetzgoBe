package com.letzgo.LetzgoBe.domain.community.comment.controller;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.community.comment.dto.req.CommentRequest;
import com.letzgo.LetzgoBe.domain.community.comment.dto.res.CommentResponse;
import com.letzgo.LetzgoBe.domain.community.comment.entity.CommentPage;
import com.letzgo.LetzgoBe.domain.community.comment.service.CommentService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/rest-api/v1/post/comment")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 API")
public class ApiV1CommentController {
    private final CommentService commentService;

    // 해당 게시글에 작성된 모든 댓글 조회
    @GetMapping("/{postId}")
    @Operation(summary = "게시글의 댓글 조회", description = "게시글의 댓글을 조회합니다.")
    public ApiResponse<List<CommentResponse>> findByPostId(@ModelAttribute CommentPage request,
                                                          @PathVariable("postId") Long postId,
                                                          @CurrentUser CurrentUserDto currentUser){
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(commentService.findByPostId(postId, pageable, currentUser));
    }

    // 댓글 좋아요
    @PostMapping("/like/{commentId}")
    @Operation(summary = "댓글 좋아요", description = "댓글을 좋아합니다.")
    public ApiResponse<Void> addCommentLike(@PathVariable("commentId") Long commentId, @CurrentUser CurrentUserDto currentUser){
        commentService.addCommentLike(commentId, currentUser);
        return ApiResponse.success();
    }

    // 댓글 좋아요 취소
    @DeleteMapping("/like/{commentId}")
    @Operation(summary = "댓글 좋아요 취소", description = "댓글 좋아요를 취소합니다.")
    public ApiResponse<Void> deleteCommentLike(@PathVariable("commentId") Long commentId, @CurrentUser CurrentUserDto currentUser){
        commentService.deleteCommentLike(commentId, currentUser);
        return ApiResponse.success();
    }

    // 해당 게시글에 댓글 생성
    @PostMapping("/{postId}")
    @Operation(summary = "게시글에 댓글 생성", description = "게시글에 댓글을 생성합니다.")
    public ApiResponse<Void> addComment(@PathVariable("postId") Long postId,
                                          @RequestBody @Valid CommentRequest commentRequest, @CurrentUser CurrentUserDto currentUser){
        commentService.addComment(postId, commentRequest, currentUser);
        return ApiResponse.success();
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ApiResponse<Void> deleteComment(@PathVariable("commentId") Long commentId, @CurrentUser CurrentUserDto currentUser){
        commentService.deleteComment(commentId, currentUser);
        return ApiResponse.success();
    }
}
