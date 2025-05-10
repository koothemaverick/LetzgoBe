package com.letzgo.LetzgoBe.domain.community.post.repositoryImpl;

import com.letzgo.LetzgoBe.domain.community.post.entity.QPostSave;
import com.letzgo.LetzgoBe.domain.community.post.repository.PostSaveQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class PostSaveQueryRepositoryImpl implements PostSaveQueryRepository {
    private final JPAQueryFactory queryFactory;

    public PostSaveQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean existsByPostIdAndMemberId(Long postId, Long memberId) {
        QPostSave postSave = QPostSave.postSave;
        Integer fetchOne = queryFactory
                .selectOne()
                .from(postSave)
                .where(
                        postSave.post.id.eq(postId),
                        postSave.member.id.eq(memberId)
                )
                .fetchFirst();
        return fetchOne != null;
    }
}
