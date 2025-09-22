package com.letzgo.LetzgoBe.domain.community.comment.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.community.comment.dto.req.CommentRequest;
import com.letzgo.LetzgoBe.domain.community.comment.dto.res.CommentResponse;
import com.letzgo.LetzgoBe.domain.community.comment.entity.Comment;
import com.letzgo.LetzgoBe.domain.community.comment.entity.CommentLike;
import com.letzgo.LetzgoBe.domain.community.comment.entity.CommentPage;
import com.letzgo.LetzgoBe.domain.community.comment.repository.CommentLikeQueryRepository;
import com.letzgo.LetzgoBe.domain.community.comment.repository.CommentRepository;
import com.letzgo.LetzgoBe.domain.community.comment.service.CommentService;
import com.letzgo.LetzgoBe.domain.community.post.entity.Post;
import com.letzgo.LetzgoBe.domain.community.post.repository.PostRepository;
import com.letzgo.LetzgoBe.domain.notification.entity.Notification;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeQueryRepository commentLikeQueryRepository;
    private final PostRepository postRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 해당 게시글에 작성된 모든 댓글 조회
    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> findByPostId(Long postId, Pageable pageable, LoginUserDto loginUser){
        checkPageSize(pageable.getPageSize());
        Page<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);
        return comments.map(comment -> convertToCommentDto(comment, loginUser));
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
        // 댓글 좋아요 이벤트 생성
        Notification notification = Notification.builder()
                .receiverId(comment.getMember().getId())
                .senderId(loginUser.getId())
                .senderNickname(loginUser.getNickname())
                .senderProfileUrl(loginUser.getProfileImageUrl())
                .objectId(commentId)
                .content("님이 댓글에 좋아요를 눌렀습니다.")
                .targetObject(Notification.TargetObject.Comment)
                .build();
        try {
            String message = objectMapper.writeValueAsString(notification);
            kafkaTemplate.send("comment-topic", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Notification: {}", e.getMessage());
        }
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
    public void addComment(Long postId, CommentRequest commentRequest, LoginUserDto loginUser){
        // 현재 로그인한 사용자의 member 객체를 가져오는 메서드
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        Comment comment = Comment.builder()
                .member(loginUser.ConvertToMember())
                .post(post)
                .content(commentRequest.getContent())
                .superCommentId(commentRequest.getSuperCommentId())
                .build();
        commentRepository.save(comment);
        // 댓글 작성 이벤트 생성
        Notification notification = Notification.builder()
                .receiverId(post.getMember().getId())
                .senderId(loginUser.getId())
                .senderNickname(loginUser.getNickname())
                .senderProfileUrl(loginUser.getProfileImageUrl())
                .objectId(postId)
                .content("님이 댓글을 작성하였습니다.")
                .targetObject(Notification.TargetObject.Post)
                .build();
        try {
            String message = objectMapper.writeValueAsString(notification);
            kafkaTemplate.send("post-topic", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Notification: {}", e.getMessage());
        }
    }

    // 해당 댓글 삭제
    @Override
    @Transactional
    public void deleteComment(Long commentId, LoginUserDto loginUser) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ServiceException(ReturnCode.COMMENT_NOT_FOUND));

        // 댓글/게시글 작성자만 삭제 가능
        Long currentUserId = loginUser.getId();
        if (!comment.getMember().getId().equals(currentUserId) && !comment.getPost().getMember().getId().equals(currentUserId)) {
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
        }
        commentRepository.deleteAll(comments);
    }

    // 해당 댓글의 모든 하위 댓글 삭제
    @Transactional
    protected void deleteChildComments(Long superCommentId) {
        List<Comment> childComments = commentRepository.findBySuperCommentId(superCommentId);
        commentRepository.deleteAll(childComments);
    }

    // 요청 페이지 수 제한
    private void checkPageSize(int pageSize) {
        int maxPageSize = CommentPage.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new ServiceException(ReturnCode.PAGE_REQUEST_FAIL);
        }
    }

    // Comment를 CommentDto로 변환
    private CommentResponse convertToCommentDto(Comment comment, LoginUserDto loginUser) {
        boolean liked = commentLikeQueryRepository.existsByMemberIdAndCommentId(loginUser.getId(), comment.getId());
        Long likeCount = commentLikeQueryRepository.countByCommentId(comment.getId());
        return CommentResponse.builder()
                .id(comment.getId())
                .memberId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .profileImageUrl(comment.getMember().getProfileImageUrl())
                .likeCount(likeCount)
                .content(comment.getContent())
                .superCommentId(comment.getSuperCommentId())
                .liked(liked)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
