package com.letzgo.LetzgoBe.domain.community.post.repositoryImpl;

import com.letzgo.LetzgoBe.domain.community.post.entity.QPostLike;
import com.letzgo.LetzgoBe.domain.community.post.repository.PostLikeQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class PostLikeQueryRepositoryImpl implements PostLikeQueryRepository {
    private final JPAQueryFactory queryFactory;

    public PostLikeQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean existsByPostIdAndMemberId(Long postId, Long memberId) {
        QPostLike postLike = QPostLike.postLike;
        Integer fetchOne = queryFactory
                .selectOne()
                .from(postLike)
                .where(
                        postLike.post.id.eq(postId),
                        postLike.member.id.eq(memberId)
                )
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public long countByPostId(Long postId) {
        QPostLike postLike = QPostLike.postLike;
        return queryFactory
                .select(postLike.count())
                .from(postLike)
                .where(postLike.post.id.eq(postId))
                .fetchOne();
    }
}
