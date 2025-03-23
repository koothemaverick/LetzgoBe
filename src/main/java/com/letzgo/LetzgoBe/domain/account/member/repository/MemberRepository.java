package com.letzgo.LetzgoBe.domain.account.member.repository;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 회원 존재여부 확인
    boolean existsByEmail(String email);

    // 이메일로 회원 찾기
    @EntityGraph(value = "Member.withAllRelations", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Member> findByEmail(String email);
}
