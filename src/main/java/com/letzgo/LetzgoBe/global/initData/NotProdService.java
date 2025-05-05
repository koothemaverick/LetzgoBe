package com.letzgo.LetzgoBe.global.initData;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberForm;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.req.ChatMessageForm;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomMember;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.PostForm;
import com.letzgo.LetzgoBe.domain.community.post.service.PostService;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.entity.Review;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.repository.ReviewRepository;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto.ConvertToLoginUserDto;
import static com.letzgo.LetzgoBe.global.initData.utils.MultipartFileUtils.getMultipartFileFromResource;

@Service
@RequiredArgsConstructor
public class NotProdService {
    private final MemberService memberService;
    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;
    private final ChatMessageService chatMessageService;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final PostService postService;

    @Transactional
    public void initDummyData() {
        // 유저 1, 2, 3, 4, 5 생성
        List<Member> members = createMembers();

        // 5명 모두 서로 팔로우하게 만들기
        createFollowRelations(members);

        // 5명 모두 게시글 1개씩 작성
        createPosts(members);

        // 유저 1이 1:1 채팅방 생성 (대상: 유저 2)
        createDmChatRoom(members);

        // 유저 1이 단체 채팅방 생성 (대상: 유저 2, 3)
        createGroupChatRoom(members);

        // 유저 1, 2의 1:1 채팅 생성
        createPrivateChatMessages(members, 1L);

        // 유저 1, 2, 3의 단체 채팅 생성
        createGroupChatMessages(members, 2L);

        // 장소 데이터 생성
        List<Place> places = createPlaceData();
        placeRepository.saveAll(places);

        // 리뷰 데이터 생성
        createReviewData(members, places);
    }

    // 유저 1, 2, 3, 4, 5 생성
    private List<Member> createMembers() {
        List<String> names = List.of("서울", "인천", "강릉", "부산", "제주");
        List<String> nicknames = List.of("seoul_gangnam", "incheon_songdo", "gangneung_beach", "busan_haeundae", "jeju_seaside");
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            MemberForm memberForm = MemberForm.builder()
                    .name(names.get(i))
                    .nickname(nicknames.get(i))
                    .phone("010-" + (i + 1) + (i + 1) + (i + 1) + (i + 1) + "-" + (i + 1) + (i + 1) + (i + 1) + (i + 1))
                    .email("user" + (i + 1) + "@example.com")
                    .password("1234")
                    .gender(i % 2 == 0 ? Member.Gender.MALE : Member.Gender.FEMALE)
                    .birthday(LocalDate.of(2001, i + 1, 1))
                    .build();
            memberService.signup(memberForm);

            Member member = memberRepository.findByEmail(memberForm.getEmail())
                    .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));
            members.add(member);
        }
        return members;
    }

    // 5명 모두 서로 팔로우하게 만들기
    private void createFollowRelations(List<Member> members) {
        for (int i = 0; i < members.size(); i++) {
            Member fromMember = members.get(i);
            LoginUserDto fromLoginUser = ConvertToLoginUserDto(fromMember);
            for (int j = 0; j < members.size(); j++) {
                if (i == j) continue;
                Member toMember = members.get(j);
                memberService.followReq(toMember.getId(), fromLoginUser);
                LoginUserDto toLoginUser = ConvertToLoginUserDto(toMember);
                memberService.acceptFollowReq(fromMember.getId(), toLoginUser);
            }
        }
    }

    // 5명 모두 게시글 1개씩 작성
    private void createPosts(List<Member> members) {
        createPost(members.get(0), "오늘은 #더현대서울 다녀왔어요! 햇살 가득한 실내 공간이 진짜 예쁘더라고요ㅎㅎ...", "static/hyundai.jpeg", "static/hyundaiburger.jpeg", 126.925978, 37.525477);
        createPost(members.get(1), "🌆 오늘은 #송도센트럴파크 나들이! 푸른 하늘 아래 우뚝 선 포스코타워가 진짜 웅장하더라고요.", "static/songdo.jpeg", "static/songdoburger.jpeg", 126.632844, 37.392872);
        createPost(members.get(2), "🌊 #강릉 #주문진해수욕장 다녀왔어요! 잔잔한 작은 해변가에서 바다 바람 맞으면서 힐링…", "static/sea.jpeg", "static/seafood.jpeg", 128.821432, 37.894882);
        createPost(members.get(3), "🏙️ #부산 #엘시티 다녀왔어요! 엘시티의 현대적이고 세련된 모습은 정말 압도적이었어요.", "static/busan.jpeg", "static/busan1.jpeg", 129.132837, 35.160736);
        createPost(members.get(4), "🌴 #제주도 여행의 완벽한 하루! 먼저 더클리프에서 바다를 바라보며 여유롭게 시간을 보냈어요.", "static/jejufood.jpeg", "static/jejucafe.jpeg", 126.560701, 33.499848);
    }

    // 게시글 생성
    private void createPost(Member member, String content, String imageFilePath1, String imageFilePath2, double mapX, double mapY) {
        PostForm postForm = PostForm.builder()
                .mapX(mapX)
                .mapY(mapY)
                .content(content)
                .build();
        List<MultipartFile> imageFiles = List.of(
                getMultipartFileFromResource(imageFilePath1, imageFilePath1.substring(imageFilePath1.lastIndexOf("/") + 1)),
                getMultipartFileFromResource(imageFilePath2, imageFilePath2.substring(imageFilePath2.lastIndexOf("/") + 1))
        );
        postService.addPost(postForm, imageFiles, ConvertToLoginUserDto(member));
    }

    // 유저 1이 1:1 채팅방 생성 (대상: 유저 2)
    private void createDmChatRoom(List<Member> members) {
        ChatRoomForm dmChatRoomForm = ChatRoomForm.builder()
                .chatRoomMembers(List.of(
                        ChatRoomMember.builder().member(members.get(1)).build()
                ))
                .build();
        chatRoomService.addChatRoom(dmChatRoomForm, LoginUserDto.ConvertToLoginUserDto(members.get(0)));
    }

    // 유저 1이 단체 채팅방 생성 (대상: 유저 2, 3)
    private void createGroupChatRoom(List<Member> members) {
        ChatRoomForm groupChatRoomForm = ChatRoomForm.builder()
                .title("테스트 채팅방")
                .chatRoomMembers(List.of(
                        ChatRoomMember.builder().member(members.get(1)).build(),
                        ChatRoomMember.builder().member(members.get(2)).build()
                ))
                .build();
        chatRoomService.addChatRoom(groupChatRoomForm, LoginUserDto.ConvertToLoginUserDto(members.get(0)));
    }

    // 유저 1, 2의 1:1 채팅
    private void createPrivateChatMessages(List<Member> members, Long chatRoomId) {
        // 유저1 -> 유저2 메시지
        ChatMessageForm chatMessageForm1 = ChatMessageForm.builder()
                .content("점심 먹었어?")
                .build();
        chatMessageService.writeChatMessage(chatRoomId, chatMessageForm1, members.get(0).getId());
        chatMessageService.readChatMessage(chatRoomId, members.get(1).getId()); // 유저2 읽음 처리

        // 유저2 -> 유저1 메시지
        ChatMessageForm chatMessageForm2 = ChatMessageForm.builder()
                .content("아직 안먹었는데 돈까스 ㄱㄱ?")
                .build();
        chatMessageService.writeChatMessage(chatRoomId, chatMessageForm2, members.get(1).getId());
    }

    // 유저 1, 2, 3의 단체 채팅
    private void createGroupChatMessages(List<Member> members, Long chatRoomId) {
        // 유저1 -> 유저2, 유저3 메시지
        ChatMessageForm chatMessageForm3 = ChatMessageForm.builder()
                .content("안녕하세요!")
                .build();
        chatMessageService.writeChatMessage(chatRoomId, chatMessageForm3, members.get(0).getId());
        chatMessageService.readChatMessage(chatRoomId, members.get(1).getId()); // 유저2 읽음 처리
        chatMessageService.readChatMessage(chatRoomId, members.get(2).getId()); // 유저3 읽음 처리

        // 유저2 -> 유저1, 유저3 메시지
        ChatMessageForm chatMessageForm4 = ChatMessageForm.builder()
                .content("안녕하세용")
                .build();
        chatMessageService.writeChatMessage(chatRoomId, chatMessageForm4, members.get(1).getId());
        chatMessageService.readChatMessage(chatRoomId, members.get(2).getId()); // 유저3 읽음 처리

        // 유저3 -> 유저1, 유저2 메시지
        ChatMessageForm chatMessageForm5 = ChatMessageForm.builder()
                .content("반가워요!!")
                .build();
        chatMessageService.writeChatMessage(chatRoomId, chatMessageForm5, members.get(2).getId());
        chatMessageService.readChatMessage(chatRoomId, members.get(1).getId()); // 유저2 읽음 처리
    }

    // 장소 데이터 생성
    private List<Place> createPlaceData() {
        // 장소 생성
        Place place1 = new Place("ChIJD3vd1Jh8ezURbh0AuKzX0Ig"); // 인천대공원
        Place place2 = new Place("ChIJQ_u_ZYF5ezURrffhNpjhTxk"); // 인천문학경기장
        Place place3 = new Place("ChIJtYSzWuZ7ezURaJ9vi-a1UB8"); // 인천광역시청
        Place place4 = new Place("ChIJbYc0JWOCezURypoST-McTYM"); // 월미도
        Place place5 = new Place("ChIJvQNefoJ3ezURNM4TFnYUTjI"); // 센트럴파크
        Place place6 = new Place("ChIJ14998Jp4ezURyTMW5UI09Jc"); // 인천 차이나타운
        Place place7 = new Place("ChIJxQc33uORezUR8hnBs0SauhA"); // 마시안해변
        Place place8 = new Place("ChIJraEdrJB4ezUR8w04ptlOUts"); // 동화마을길
        Place place9 = new Place("ChIJG0A3n-t7ezUR940fTAL73Rg"); // 소래습지 생태공원
        Place place10 = new Place("ChIJv8ZRJQx5fDUR1-3A90_SkA0"); // 옥토끼 우주센터
        Place place11 = new Place("ChIJi9mzC0t8ezURlI-t4_nvB7s"); // 부평역 지하상가

        // List로 반환
        return List.of(place1, place2, place3, place4, place5, place6, place7, place8, place9, place10, place11);
    }

    // 리뷰 데이터 생성
    private void createReviewData(List<Member> members, List<Place> places) {
        // 각 유저에 대해 8개의 리뷰 생성
        for (Member member : members) {
            List<Place> shuffledPlaces = new ArrayList<>(places);
            Collections.shuffle(shuffledPlaces);
            for (int i = 0; i < 8; i++) {
                Place place = shuffledPlaces.get(i); // 앞에서 8개만 선택

                Review review = Review.builder()
                        .member(member)
                        .place(place)
                        .title(member.getNickname() + "의 리뷰 " + (i + 1))
                        .content("review 입니다.")
                        .rating((i % 5) + 1)
                        .build();
                reviewRepository.save(review);
            }
        }
    }
}
