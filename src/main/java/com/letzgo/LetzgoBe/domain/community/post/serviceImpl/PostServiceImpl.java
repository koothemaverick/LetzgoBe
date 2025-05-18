package com.letzgo.LetzgoBe.domain.community.post.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberFollowRepository;
import com.letzgo.LetzgoBe.domain.community.comment.repository.CommentRepository;
import com.letzgo.LetzgoBe.domain.community.comment.service.CommentService;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.PostForm;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.XYForm;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.DetailPostDto;
import com.letzgo.LetzgoBe.domain.community.post.dto.res.PostDto;
import com.letzgo.LetzgoBe.domain.community.post.entity.Post;
import com.letzgo.LetzgoBe.domain.community.post.entity.PostLike;
import com.letzgo.LetzgoBe.domain.community.post.entity.PostPage;
import com.letzgo.LetzgoBe.domain.community.post.entity.PostSave;
import com.letzgo.LetzgoBe.domain.community.post.repository.PostLikeQueryRepository;
import com.letzgo.LetzgoBe.domain.community.post.repository.PostLikeRepository;
import com.letzgo.LetzgoBe.domain.community.post.repository.PostRepository;
import com.letzgo.LetzgoBe.domain.community.post.repository.PostSaveQueryRepository;
import com.letzgo.LetzgoBe.domain.community.post.service.PostService;
import com.letzgo.LetzgoBe.domain.notification.entity.Notification;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import com.letzgo.LetzgoBe.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostLikeQueryRepository postLikeQueryRepository;
    private final PostSaveQueryRepository postSaveQueryRepository;
    private final CommentService commentService;
    private final S3Service s3Service;
    private final CommentRepository commentRepository;
    private final MemberFollowRepository memberFollowRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 본인 & 팔로우한 유저 & 유저(1,2,3,4,5)의 게시글 조회
    @Override
    @Transactional(readOnly = true)
    public Page<DetailPostDto> getMainPost(LoginUserDto loginUser, Pageable pageable){
        checkPageSize(pageable.getPageSize());
        Long loginUserId = loginUser.getId();
        List<Long> followingMemberIds = memberFollowRepository.findFollowedMemberIdsByFollowerId(loginUserId);
        // 기본 유저 ID: 1, 2, 3, 4, 5 (단, 본인 ID는 제외)
        List<Long> defaultMemberIds = Arrays.asList(1L, 2L, 3L, 4L, 5L)
                .stream()
                .filter(id -> !id.equals(loginUserId))
                .collect(Collectors.toList());
        // 본인 ID + 팔로우한 사람들 ID + 기본 유저 ID 포함한 리스트 생성 (중복 제거)
        Set<Long> targetMemberIdSet = new HashSet<>();
        targetMemberIdSet.addAll(followingMemberIds);
        targetMemberIdSet.addAll(defaultMemberIds);
        targetMemberIdSet.add(loginUserId);
        // Set<Long> -> List<Long> 변환
        List<Long> targetMemberIds = new ArrayList<>(targetMemberIdSet);
        Page<Post> posts = postRepository.findByMemberIdInOrderByCreatedAtDesc(targetMemberIds, pageable);
        return posts.map(post -> convertToDetailPostDto(post, loginUser));
    }

    // 사용자 위치 주변 게시글(관광지&사용자) 조회
    @Override
    @Transactional(readOnly = true)
    public Page<DetailPostDto> findPostsWithinRadius(XYForm xyForm, Pageable pageable, LoginUserDto loginUser){
        checkPageSize(pageable.getPageSize());
        Page<Post> posts = postRepository.findPostsWithinRadius(
                xyForm.getMapX(), xyForm.getMapY(), xyForm.getRadius(), pageable
        );
        return posts.map(post -> convertToDetailPostDto(post, loginUser));
    }

    // 해당 사용자가 작성한 게시글 조회
    @Override
    @Transactional(readOnly = true)
    public Page<DetailPostDto> findByMemberId(Long memberId, Pageable pageable, LoginUserDto loginUser){
        checkPageSize(pageable.getPageSize());
        Page<Post> posts = postRepository.findByMemberId(memberId, pageable);
        return posts.map(post -> convertToDetailPostDto(post, loginUser));
    }

    // 해당 게시글 상세 조회
    @Override
    @Transactional(readOnly = true)
    public DetailPostDto findById(Long postId, LoginUserDto loginUser){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        return convertToDetailPostDto(post, loginUser);
    }

    // 해당 사용자가 저장한 게시글 조회
    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> getSavedPostByMember(Long memberId, Pageable pageable) {
        checkPageSize(pageable.getPageSize());
        Page<Post> posts = postRepository.findSavedPostByMemberId(memberId, pageable);
        return posts.map(this::convertToPostDto);
    }

    // 사용자 닉네임 & 게시글 내용 검색
    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> searchByKeyword(String keyword, Pageable pageable){
        checkPageSize(pageable.getPageSize());
        Page<Post> posts = postRepository.searchByKeyword(keyword, pageable);
        return posts.map(this::convertToPostDto);
    }

    // 해당 게시글 저장
    @Override
    @Transactional
    public void addCollectionPost(Long postId, LoginUserDto loginUser){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        boolean alreadySaved = post.getSavedMembers()
                .stream()
                .anyMatch(postSave -> postSave.getMember() !=null && postSave.getMember().getId().equals(loginUser.getId()));
        if (alreadySaved) {
            throw new ServiceException(ReturnCode.POST_ALREADY_SAVED);
        }
        PostSave postSave = new PostSave(loginUser.ConvertToMember(), post);
        post.getSavedMembers().add(postSave);
    }

    // 해당 게시글 저장 취소
    @Override
    @Transactional
    public void deleteCollectionPost(Long postId, LoginUserDto loginUser){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        PostSave postSave = post.getSavedMembers()
                .stream()
                .filter(m -> m.getMember() != null && m.getMember().getId().equals(loginUser.getId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        post.getSavedMembers().remove(postSave);
    }

    // 게시글 좋아요
    @Override
    @Transactional
    public void addPostLike(Long postId, LoginUserDto loginUser){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        boolean alreadyLiked = post.getLikedMembers()
                .stream()
                .anyMatch(postLike -> postLike.getMember() != null && postLike.getMember().getId().equals(loginUser.getId()));
        if (alreadyLiked) {
            throw new ServiceException(ReturnCode.POST_ALREADY_LIKED);
        }
        PostLike postLike = new PostLike(loginUser.ConvertToMember(), post);
        post.getLikedMembers().add(postLike);
        // 게시글 좋아요 이벤트 생성
        Notification notification = Notification.builder()
                .senderId(loginUser.getId())
                .objectId(postId)
                .content(loginUser.getName() + "님이 게시글에 좋아요를 눌렀습니다")
                .targetObject(Notification.TargetObject.Post)
                .build();
        try {
            String message = objectMapper.writeValueAsString(notification);
            kafkaTemplate.send("comment-topic", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Notification: {}", e.getMessage());
        }
    }

    // 게시글 좋아요 취소
    @Override
    @Transactional
    public void deletePostLike(Long postId, LoginUserDto loginUser){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        PostLike postLike = post.getLikedMembers()
                .stream()
                .filter(m -> m.getMember()!=null && m.getMember().getId().equals(loginUser.getId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        post.getLikedMembers().remove(postLike);
    }

    // 게시글 생성
    @Override
    @Transactional
    public void addPost(PostForm postForm, List<MultipartFile> imageFiles, LoginUserDto loginUser) {
        // 입력 받은 이미지들 S3에 저장
        List<String> imageUrls = new ArrayList<>();
        if (imageFiles.size() > 5 || imageFiles.isEmpty() || imageFiles == null) {
            throw new ServiceException(ReturnCode.FILE_UPLOAD_ERROR);
        } else{
            for (MultipartFile imageFile : imageFiles) {
                try {
                    String imageUrl = s3Service.uploadFile(imageFile, "post-images");
                    imageUrls.add(imageUrl);
                } catch (IOException e) {
                    throw new ServiceException(ReturnCode.INTERNAL_ERROR);
                }
            }
        }
        Post post = Post.builder()
                .member(loginUser.ConvertToMember())
                .content(postForm.getContent())
                .mapX(postForm.getMapX())
                .mapY(postForm.getMapY())
                .imageUrls(imageUrls)
                .build();
        postRepository.save(post);
    }

    // 해당 게시글 수정
    @Override
    @Transactional
    public void updatePost(Long postId, PostForm postForm, List<MultipartFile> imageFiles, LoginUserDto loginUser) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        // 작성자 검증 - 현재 로그인한 사용자의 ID를 가져와서 검증
        if (!post.getMember().getId().equals(loginUser.getId())) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }

        // 기존 이미지들 삭제 후 입력 받은 이미지들 S3에 저장
        List<String> imageUrls = new ArrayList<>();
        if (imageFiles.size() > 5 || imageFiles.isEmpty() || imageFiles == null) {
            throw new ServiceException(ReturnCode.FILE_UPLOAD_ERROR);
        } else{
            s3Service.deleteAllFile(post.getImageUrls());
            for (MultipartFile imageFile : imageFiles) {
                try {
                    String imageUrl = s3Service.uploadFile(imageFile, "post-images");
                    imageUrls.add(imageUrl);
                } catch (IOException e) {
                    throw new ServiceException(ReturnCode.INTERNAL_ERROR);
                }
            }
        }
        post.setMapX(postForm.getMapX());
        post.setMapY(postForm.getMapY());
        post.setContent(postForm.getContent());
        post.setImageUrls(imageUrls);
        postRepository.save(post);
    }

    // 해당 게시글 삭제
    @Override
    @Transactional
    public void deletePost(Long postId, LoginUserDto loginUser) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ServiceException(ReturnCode.POST_NOT_FOUND));
        // 작성자 검증 - 현재 로그인한 사용자의 ID를 가져와서 검증
        if (!post.getMember().getId().equals(loginUser.getId())) {
            throw new ServiceException(ReturnCode.NOT_AUTHORIZED);
        }
        commentService.deleteAllComments(postId);
        s3Service.deleteAllFile(post.getImageUrls());
        postRepository.delete(post);
    }

    // 해당 멤버가 작성한 모든 게시글 삭제
    @Override
    @Transactional
    public void deleteMembersAllPosts(Long memberId){
        List<Post> posts = postRepository.findPostsByMemberId(memberId);
        for (Post post : posts){
            commentService.deleteAllComments(post.getId());
            s3Service.deleteAllFile(post.getImageUrls());
            postRepository.delete(post);
        }
    }

    // 요청 페이지 수 제한
    public void checkPageSize(int pageSize) {
        int maxPageSize = PostPage.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new ServiceException(ReturnCode.PAGE_REQUEST_FAIL);
        }
    }

    // Post를 PostDto로 변환
    private PostDto convertToPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .memberId(post.getMember().getId())
                .nickname(post.getMember().getNickname())
                .mapX(post.getMapX())
                .mapY(post.getMapY())
                .imageUrls(post.getImageUrls())
                .createdAt(post.getCreatedAt())
                .build();
    }

    // Post를 DetailPostDto로 변환
    private DetailPostDto convertToDetailPostDto(Post post, LoginUserDto loginUser) {
        boolean liked = postLikeQueryRepository.existsByPostIdAndMemberId(post.getId(), loginUser.getId());
        Long likeCount = postLikeQueryRepository.countByPostId(post.getId());
        boolean saved = postSaveQueryRepository.existsByPostIdAndMemberId(post.getId(), loginUser.getId());
        return DetailPostDto.builder()
                .id(post.getId())
                .memberId(post.getMember().getId())
                .profileImageUrl(post.getMember().getProfileImageUrl())
                .nickname(post.getMember().getNickname())
                .likeCount(likeCount)
                .commentCount(commentRepository.countPostComment(post.getId()))
                .mapX(post.getMapX())
                .mapY(post.getMapY())
                .content(post.getContent())
                .imageUrls(post.getImageUrls())
                .liked(liked)
                .saved(saved)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
