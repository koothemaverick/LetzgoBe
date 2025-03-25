package com.letzgo.LetzgoBe.domain.community.comment.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.community.comment.dto.req.CommentForm;
import com.letzgo.LetzgoBe.domain.community.comment.dto.res.CommentDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    // 해당 게시글에 작성된 모든 댓글 조회
    Page<CommentDto> findByPostId(Long postId, Pageable pageable);

    // 댓글 좋아요
    void addCommentLike(Long commentId, LoginUserDto loginUser);

    // 댓글 좋아요 취소
    void deleteCommentLike(Long commentId, LoginUserDto loginUser);

    // 해당 게시글에 댓글 생성
    void addComment(Long commPostId, @Valid CommentForm commentForm, LoginUserDto loginUser);

    // 해당 댓글 수정
    void updateComment(Long commentId,@Valid CommentForm commentForm, LoginUserDto loginUser);

    // 해당 댓글 삭제
    void deleteComment(Long commentId, LoginUserDto loginUser);

    // 해당 게시글의 모든 댓글 삭제
    void deleteAllComments(Long commPostId);
}
