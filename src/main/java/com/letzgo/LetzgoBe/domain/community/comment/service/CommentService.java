package com.letzgo.LetzgoBe.domain.community.comment.service;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.community.comment.dto.req.CommentRequest;
import com.letzgo.LetzgoBe.domain.community.comment.dto.res.CommentResponse;
import com.letzgo.LetzgoBe.global.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    // 해당 게시글에 작성된 모든 댓글 조회
    PageResponse<CommentResponse> findByPostId(Long postId, Pageable pageable, CurrentUserDto currentUser);

    // 댓글 좋아요
    void addCommentLike(Long commentId, CurrentUserDto currentUser);

    // 댓글 좋아요 취소
    void deleteCommentLike(Long commentId, CurrentUserDto currentUser);

    // 해당 게시글에 댓글 생성
    void addComment(Long commPostId, CommentRequest commentRequest, CurrentUserDto currentUser);

    // 해당 댓글 삭제
    void deleteComment(Long commentId, CurrentUserDto currentUser);

    // 해당 게시글의 모든 댓글 삭제
    void deleteAllComments(Long commPostId);

    // 해당 멤버가 작성한 모든 댓글 삭제
    void deleteMembersAllComments(Long memberId);
}
