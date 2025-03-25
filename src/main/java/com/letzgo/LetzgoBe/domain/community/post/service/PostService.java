package com.letzgo.LetzgoBe.domain.community.post.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.PostForm;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.XYForm;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.DetailPostDto;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.PostDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    // 사용자 위치 주변 게시글(관광지&사용자) 조회
    Page<DetailPostDto> findPostsWithinRadius(@Valid XYForm xyForm, Pageable pageable);

    // 해당 사용자가 작성한 게시글 조회
    Page<DetailPostDto> findByMemberId(Long memberId, Pageable pageable);

    // 해당 게시글 상세 조회
    DetailPostDto findById(Long postId);

    // 해당 사용자가 저장한 게시글 조회
    Page<PostDto> getSavedPostByMember(Long memberId, Pageable pageable);

    // 사용자 닉네임 & 게시글 내용 검색
    Page<PostDto> searchByKeyword(String keyword, Pageable pageable);

    // 해당 게시글 저장
    void addCollectionPost(Long postId, LoginUserDto loginUser);

    // 해당 게시글 저장 취소
    void deleteCollectionPost(Long postId, LoginUserDto loginUser);

    // 게시글 좋아요
    void addPostLike(Long postId, LoginUserDto loginUser);

    // 게시글 좋아요 취소
    void deletePostLike(Long postId, LoginUserDto loginUser);

    // 게시글 생성
    void addPost(@Valid PostForm postForm, List<MultipartFile> imageFiles, LoginUserDto loginUser);

    // 해당 게시글 수정
    void updatePost(Long postId, @Valid PostForm postForm, List<MultipartFile> imageFiles, LoginUserDto loginUser);

    // 해당 게시글 삭제
    void deletePost(Long postId, LoginUserDto loginUser);
}
