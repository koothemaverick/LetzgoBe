package com.letzgo.LetzgoBe.domain.community.comment.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.community.comment.dto.req.CommentForm;
import com.letzgo.LetzgoBe.domain.community.comment.dto.res.CommentDto;
import com.letzgo.LetzgoBe.domain.community.comment.entity.CommentPage;
import com.letzgo.LetzgoBe.domain.community.comment.service.CommentService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.common.response.LetzgoPage;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value="/rest-api/v1/post/comment")
@RequiredArgsConstructor
public class ApiV1CommentController {
    private final CommentService commentService;

    // 해당 게시글에 작성된 모든 댓글 조회
    @GetMapping("/{postId}")
    public ApiResponse<CommentDto> findByPostId(@ModelAttribute CommentPage request, @PathVariable("postId") Long postId){
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.of(LetzgoPage.of(commentService.findByPostId(postId, pageable)));
    }

    // 댓글 좋아요
    @PostMapping("/like/{commentId}")
    public ApiResponse<String> addCommentLike(@PathVariable("commentId") Long commentId, @LoginUser LoginUserDto loginUser){
        commentService.addCommentLike(commentId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 댓글 좋아요 취소
    @DeleteMapping("/like/{commentId}")
    public ApiResponse<String> deleteCommentLike(@PathVariable("commentId") Long commentId, @LoginUser LoginUserDto loginUser){
        commentService.deleteCommentLike(commentId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 해당 게시글에 댓글 생성
    @PostMapping("/{postId}")
    public ApiResponse<String> addComment(@PathVariable("postId") Long postId,
                                          @RequestBody @Valid CommentForm commentForm, @LoginUser LoginUserDto loginUser){
        commentService.addComment(postId, commentForm, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ApiResponse<String> updateComment(@PathVariable("commentId") Long commentId,
                                             @RequestBody @Valid CommentForm commentForm, @LoginUser LoginUserDto loginUser){
        commentService.updateComment(commentId, commentForm, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ApiResponse<String> deleteComment(@PathVariable("commentId") Long commentId, @LoginUser LoginUserDto loginUser){
        commentService.deleteComment(commentId, loginUser);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }
}
