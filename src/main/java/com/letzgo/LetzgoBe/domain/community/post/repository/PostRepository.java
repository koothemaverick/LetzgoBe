package com.letzgo.LetzgoBe.domain.community.post.repository;

import com.letzgo.LetzgoBe.domain.community.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 사용자 위치 주변 게시글(관광지&사용자) 조회
    @Query(value = """
        SELECT * FROM post
        WHERE earth_distance(ll_to_earth(:mapY, :mapX), ll_to_earth(mapy, mapx)) <= :radius
        ORDER BY create_date DESC
        """, nativeQuery = true)
    Page<Post> findPostsWithinRadius(@Param("mapX") Double mapX, @Param("mapY") Double mapY, @Param("radius") Integer radius, Pageable pageable);

    // 본인 게시글 최신순 조회
    @Query("SELECT c FROM Post c WHERE c.member.id = :memberId ORDER BY c.createdAt DESC")
    Page<Post> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 사용자가 저장한 게시글 조회
    @Query("SELECT p FROM Post p JOIN p.savedMembers sm WHERE sm.member.id = :memberId")
    Page<Post> findSavedPostByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 사용자 닉네임 & 게시글 내용 검색
    @Query("SELECT c FROM Post c WHERE c.member.nickname LIKE %:keyword% OR c.content LIKE %:keyword%")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 해당 멤버가 작성한 모든 게시글 삭제
    List<Post> findPostsByMemberId(Long memberId);
}
