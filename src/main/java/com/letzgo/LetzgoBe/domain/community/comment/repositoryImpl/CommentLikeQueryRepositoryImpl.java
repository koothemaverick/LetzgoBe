package com.letzgo.LetzgoBe.domain.community.comment.repositoryImpl;

import com.letzgo.LetzgoBe.domain.community.comment.repository.CommentLikeQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import static com.letzgo.LetzgoBe.domain.community.comment.entity.QCommentLike.commentLike;

@Repository
public class CommentLikeQueryRepositoryImpl implements CommentLikeQueryRepository {
    private final JPAQueryFactory queryFactory;

    public CommentLikeQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean existsByMemberIdAndCommentId(Long memberId, Long commentId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(commentLike)
                .where(
                        commentLike.member.id.eq(memberId),
                        commentLike.comment.id.eq(commentId)
                )
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public long countByCommentId(Long commentId) {
        return queryFactory
                .select(commentLike.count())
                .from(commentLike)
                .where(commentLike.comment.id.eq(commentId))
                .fetchOne();
    }
}
