package com.letzgo.LetzgoBe.domain.account.member.repository;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.entity.MemberFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberFollowRepository extends JpaRepository<MemberFollow, Long> {
    // 팔로우 관계 존재여부 확인
    boolean existsByFollowAndFollowed(Member follow, Member followed);

    // 팔로우 관계 가져오기
    Optional<MemberFollow> findByFollowAndFollowed(Member follow, Member followed);
}
