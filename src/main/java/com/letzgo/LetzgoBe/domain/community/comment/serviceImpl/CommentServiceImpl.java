package com.letzgo.LetzgoBe.domain.community.comment.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.community.comment.dto.req.CommentForm;
import com.letzgo.LetzgoBe.domain.community.comment.dto.res.CommentDto;
import com.letzgo.LetzgoBe.domain.community.comment.entity.Comment;
import com.letzgo.LetzgoBe.domain.community.comment.entity.CommentLike;
import com.letzgo.LetzgoBe.domain.community.comment.entity.CommentPage;
import com.letzgo.LetzgoBe.domain.community.comment.repository.CommentRepository;
import com.letzgo.LetzgoBe.domain.community.comment.service.CommentService;
import com.letzgo.LetzgoBe.domain.community.post.entity.Post;
import com.letzgo.LetzgoBe.domain.community.post.repository.PostRepository;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 해당 게시글에 작성된 모든 댓글 조회
    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> findByPostId(Long postId, Pageable pageable){
        checkPageSize(pageable.getPageSize());
        Page<Comment> comments = commentRepository.findByPostIdOrderByCreateDateDesc(postId, pageable);
        return comments.map(this::convertToCommentDto);
    }

    // 댓글 좋아요
    @Override
    @Transactional
    public void addCommentLike(Long commentId, LoginUserDto loginUser){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ServiceException(ReturnCode.COMMENT_NOT_FOUND));
        boolean alreadyLiked = comment.getLikedMembers()
                .stream()
                .anyMatch(commentLike -> commentLike.getMember() != null && commentLike.getMember().getId().equals(loginUser.getId()));
        if (alreadyLiked) {
            throw new ServiceException(ReturnCode.COMMENT_ALREADY_LIKED);
        }
        CommentLike commentLike = new CommentLike(loginUser.ConvertToMember(), comment);
        comment.getLikedMembers().add(commentLike);
    }

    // 댓글 좋아요 취소
    @Override
    @Transactional
    public void deleteCommentLike(Long commentId, LoginUserDto loginUser) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ServiceException(ReturnCode.COMMENT_NOT_FOUND));
        CommentLike commentLike = comment.getLikedMembers()
                .stream()
                .filter(m -> m.getMember()!=null && m.getMember().getId().equals(loginUser.getId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ReturnCode.COMMENT_NOT_FOUND));
        comment.getLikedMembers().remove(commentLike);
    }

    // 해당 게시글에 댓글 생성
    @Override
    @Transactional
    public void addComment(Long postId, CommentForm commentForm, LoginUserDto loginUser){
        // 현재 로그인한 사용자의 member 객체를 가져오는 메서드
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        Comment comment = Comment.builder()
                .member(loginUser.ConvertToMember())
                .post(post)
                .content(commentForm.getContent())
                .superCommentId(commentForm.getSuperCommentId())
                .build();
        commentRepository.save(comment);
    }

    // 해당 댓글 수정
    @Override
    @Transactional
    public void updateComment(Long commentId, CommentForm commentForm, LoginUserDto loginUser){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ServiceException(ReturnCode.COMMENT_NOT_FOUND));

        // 작성자 검증 - 현재 로그인한 사용자의 ID를 가져와서 검증
        if (!comment.getMember().getId().equals(loginUser.getId())) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }
        comment.setContent(commentForm.getContent());
        commentRepository.save(comment);
    }

    // 해당 댓글 삭제
    @Override
    @Transactional
    public void deleteComment(Long commentId, LoginUserDto loginUser) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ServiceException(ReturnCode.COMMENT_NOT_FOUND));

        // 작성자 검증 - 현재 로그인한 사용자의 ID를 가져와서 검증
        Long currentUserId = loginUser.getId();
        if (!comment.getMember().getId().equals(currentUserId)) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }
        deleteChildComments(commentId);
        commentRepository.save(comment);  // 답글 삭제 후 더티체킹
        commentRepository.delete(comment);
    }

    // 해당 게시글의 모든 댓글 삭제
    @Override
    @Transactional
    public void deleteAllComments(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        List<Comment> comments = commentRepository.findByPost(post);
        commentRepository.deleteAll(comments);
    }

    // 해당 멤버가 작성한 모든 댓글 삭제
    @Override
    @Transactional
    public void deleteMembersAllComments(Long memberId){
        List<Comment> comments = commentRepository.findByMemberId(memberId);
        for (Comment comment : comments) {
            deleteChildComments(comment.getId());
            commentRepository.save(comment);
        }
        commentRepository.deleteAll(comments);
    };

    // 해당 댓글의 모든 하위 댓글 삭제
    @Transactional
    protected void deleteChildComments(Long superCommentId) {
        List<Comment> childComments = commentRepository.findBySuperCommentId(superCommentId);
        for (Comment childComment : childComments) {
            commentRepository.delete(childComment);
        }
    }

    // 요청 페이지 수 제한
    private void checkPageSize(int pageSize) {
        int maxPageSize = CommentPage.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new ServiceException(ReturnCode.PAGE_REQUEST_FAIL);
        }
    }

    // Comment를 CommentDto로 변환
    private CommentDto convertToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .memberId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .profileImageUrl(comment.getMember().getProfileImageUrl())
                .likeCount(comment.getLikedMembers().size())
                .content(comment.getContent())
                .superCommentId(comment.getSuperCommentId())
                .createDate(comment.getCreateDate())
                .build();
    }
}
