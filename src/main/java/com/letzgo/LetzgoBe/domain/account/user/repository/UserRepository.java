package com.letzgo.LetzgoBe.domain.account.user.repository;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 회원 존재여부 확인
    boolean existsByEmail(String email);

    // 이메일로 회원 찾기
    Optional<User> findByEmail(String email);
}
