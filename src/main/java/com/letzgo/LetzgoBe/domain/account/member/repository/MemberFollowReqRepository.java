package com.letzgo.LetzgoBe.domain.account.member.repository;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.entity.MemberFollowReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberFollowReqRepository extends JpaRepository<MemberFollowReq, Long> {
    // 팔로우 요청 존재여부 확인
    boolean existsByFollowReqAndFollowRec(Member followReq, Member followRec);

    // 팔로우 요청 가져오기
    Optional<MemberFollowReq> findByFollowReqIdAndFollowRecId(Long followReqId, Long followRecId);

    // 조건 기반 삭제 (JPQL)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from MemberFollowReq m " +
            "where m.followReq.id = :followReqId and m.followRec.id = :followRecId")
    int deleteByFollowReqIdAndFollowRecId(@Param("followReqId") Long followReqId,
                                           @Param("followRecId") Long followRecId);
}
