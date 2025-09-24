package com.letzgo.LetzgoBe.domain.community.post.service;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.PostRequest;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.XYRequest;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.DetailPostResponse;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.PostResponse;
import com.letzgo.LetzgoBe.global.common.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    // 본인 & 팔로우한 유저의 게시글 조회
    PageResponse<DetailPostResponse> getMainPost(CurrentUserDto currentUser, Pageable pageable);

    // 사용자 위치 주변 게시글(관광지&사용자) 조회
    PageResponse<DetailPostResponse> findPostsWithinRadius(XYRequest xyRequest, Pageable pageable, CurrentUserDto currentUser);

    // 해당 사용자가 작성한 게시글 조회
    PageResponse<DetailPostResponse> findByMemberId(Long memberId, Pageable pageable, CurrentUserDto currentUser);

    // 해당 게시글 상세 조회
    DetailPostResponse findById(Long postId, CurrentUserDto currentUser);

    // 해당 사용자가 저장한 게시글 조회
    PageResponse<PostResponse> getSavedPostByMember(Long memberId, Pageable pageable);

    // 사용자 닉네임 & 게시글 내용 검색
    PageResponse<PostResponse> searchByKeyword(String keyword, Pageable pageable);

    // 해당 게시글 저장
    void addCollectionPost(Long postId, CurrentUserDto currentUser);

    // 해당 게시글 저장 취소
    void deleteCollectionPost(Long postId, CurrentUserDto currentUser);

    // 게시글 좋아요
    void addPostLike(Long postId, CurrentUserDto currentUser);

    // 게시글 좋아요 취소
    void deletePostLike(Long postId, CurrentUserDto currentUser);

    // 게시글 생성
    void addPost(PostRequest postRequest, List<MultipartFile> imageFiles, CurrentUserDto currentUser);

    // 해당 게시글 수정
    void updatePost(Long postId, PostRequest postRequest, List<MultipartFile> imageFiles, CurrentUserDto currentUser);

    // 해당 게시글 삭제
    void deletePost(Long postId, CurrentUserDto currentUser);

    // 해당 멤버가 작성한 모든 게시글 삭제
    void deleteMembersAllPosts(Long memberId);
}
