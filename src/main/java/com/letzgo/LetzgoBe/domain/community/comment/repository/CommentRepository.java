package com.letzgo.LetzgoBe.domain.community.comment.repository;

import com.letzgo.LetzgoBe.domain.community.comment.entity.Comment;
import com.letzgo.LetzgoBe.domain.community.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 해당 게시글에 작성된 모든 댓글 페이지 조회
    Page<Comment> findByPostIdOrderByCreateDateDesc(Long postId, Pageable pageable);

    // 해당 댓글의 모든 하위 댓글 삭제
    List<Comment> findBySuperCommentId(Long superCommentId);

    // 해당 게시글에 작성된 모든 댓글 리스트 조회
    List<Comment> findByPost(Post post);

    // 해당 게시글에 작성된 댓글 개수 조회
    @Query("""
select count(c) from Comment c
where c.post.id = :postId
""")
    long countPostComment(@Param("postId")Long postId);

    // 해당 멤버가 작성한 모든 댓글 삭제
    List<Comment> findByMemberId(Long memberId);
}
