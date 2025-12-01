package com.letzgo.LetzgoBe.global.initData;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.account.member.converter.MemberConverter;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberRequest;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomRequest;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomMember;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.domain.community.post.dto.req.PostRequest;
import com.letzgo.LetzgoBe.domain.community.post.service.PostService;
import com.letzgo.LetzgoBe.domain.dataFetcher.scheduler.DataFetchSchedule;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.entity.Review;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.repository.ReviewRepository;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import com.letzgo.LetzgoBe.global.initData.utils.CommentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.letzgo.LetzgoBe.global.initData.utils.MultipartFileUtils.getMultipartFileFromResource;

@Slf4j
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
    private final CommentUtils commentUtils;
    private final DataFetchSchedule dataFetchSchedule;
    private final MemberConverter memberConverter;

    @Transactional
    public void initDummyData() {
        // 유저 1, 2, 3, 4, 5 생성
        List<Member> members = createMembers();

        // 5명 모두 서로 팔로우하게 만들기
        createFollowRelations(members);

        // 5명 모두 게시글 1개씩 작성
        createPosts(members);

        // 유저 3, 4, 5가 모든 게시글에 대해 좋아요 누름
        createPostLikes(members);

        // 모든 게시글에 서로 댓글&답글 작성
        createComments(members);

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

        // 스케줄러 수동 실행 (테스트 및 서버 배포용)
//        runDataFetchScheduler();
    }

    // 유저 1, 2, 3, 4, 5 생성
    private List<Member> createMembers() {
        List<String> names = List.of("서울", "인천", "강릉", "부산", "제주");
        List<String> nicknames = List.of("seoul_gangnam", "incheon_songdo", "gangneung_beach", "busan_haeundae", "jeju_seaside");
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            MemberRequest memberRequest = MemberRequest.builder()
                    .name(names.get(i))
                    .nickname(nicknames.get(i))
                    .phone("010-" + (i + 1) + (i + 1) + (i + 1) + (i + 1) + "-" + (i + 1) + (i + 1) + (i + 1) + (i + 1))
                    .email("user" + (i + 1) + "@example.com")
                    .password("1234")
                    .gender(i % 2 == 0 ? Member.Gender.MALE : Member.Gender.FEMALE)
                    .birthday(LocalDate.of(2001, i + 1, 1))
                    .build();
            memberService.signup(memberRequest);

            Member member = memberRepository.findByEmail(memberRequest.getEmail())
                    .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));
            members.add(member);
        }

        //이메일인증 테스트용 유저6
        MemberRequest memberRequest = MemberRequest.builder()
                .name("test6")
                .nickname("test6")
                .phone("010-1234-4321")
                .email("uichan0610@gmail.com")
                .password("1234")
                .gender(Member.Gender.MALE)
                .birthday(LocalDate.of(2001,01,01))
                .build();
        memberService.signup(memberRequest);

        Member member = memberRepository.findByEmail(memberRequest.getEmail())
                .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));
        members.add(member);

        return members;
    }

    // 5명 모두 서로 팔로우하게 만들기
    private void createFollowRelations(List<Member> members) {
        for (int i = 0; i < members.size(); i++) {
            Member fromMember = members.get(i);
            CurrentUserDto fromCurrentUser = memberConverter.toCurrentUserDto(fromMember);
            for (int j = 0; j < members.size(); j++) {
                if (i == j) continue;
                Member toMember = members.get(j);
                memberService.followReq(toMember.getId(), fromCurrentUser);
                CurrentUserDto toLoginUser = memberConverter.toCurrentUserDto(toMember);
                memberService.acceptFollowReq(fromMember.getId(), toLoginUser);
            }
        }
    }

    // 5명 모두 게시글 1개씩 작성
    private void createPosts(List<Member> members) {
        createPost(members.get(0),     "오늘은 더현대서울 다녀왔어요! 햇살 가득한 실내 공간이 진짜 예쁘더라고요ㅎㅎ 여유롭게 돌아다니며 구경하다가 출출해서 들른 곳은 #다운타운버거 햄버거 맛집! 고기 패티도 두툼하고 소스도 찰떡이라 만족스러운 한 끼였어요. 쇼핑도 좋지만 이렇게 분위기 좋은 공간에서 힐링하는 맛이 있는 듯☺️ 다음엔 카페 투어도 해보고 싶네요!\n\n#더현대서울맛집 #서울핫플 #햄버거맛집 #힐링시간", "static/hyundai.jpeg", "static/hyundaiburger.jpeg", 126.925978, 37.525477);
        createPost(members.get(1), "🌆 오늘은 #송도센트럴파크 나들이! 푸른 하늘 아래 우뚝 선 포스코타워가 진짜 웅장하더라고요. 사진 찍기 딱 좋은 스팟ㅎㅎ 산책하다가 출출해서 들른 곳은 바로 #GTS햄버거 🍔 고기 두툼하고 소스 조합이 미쳤어요...! 공원도 걷고 맛집도 즐기고 힐링 제대로 한 하루☺️ 다음엔 오리배도 꼭 타봐야겠다.\n\n#송도맛집 #포스코타워 #센트럴파크산책 #햄버거맛집 #힐링여행 #인천핫플", "static/songdo.jpeg", "static/songdoburger.jpeg", 126.632844, 37.392872);
        createPost(members.get(2), "🌊 #강릉 #주문진해수욕장 다녀왔어요! 잔잔한 작은 해변가에서 바다 바람 맞으면서 힐링… 파도 소리에 마음까지 씻기는 기분이었어요 그리고 역시 주문진에서는 회를 먹어야 제맛이죠! 싱싱한 회 한 접시, 대게, 그리고 뜨끈한 해물라면에 소주 한 잔까지… 완벽한 코스ㅎㅎ 바다 보면서 먹으니 진짜 꿀맛이었어요🥹 다음에도 친구들이랑 또 오기로 약속! 강릉 바다+먹방 여행 추천합니다.\n\n#주문진맛집 #해산물파티 #강릉여행 #회맛집 #해변산책 #먹방투어 #강릉핫플", "static/sea.jpeg", "static/seafood.jpeg", 128.821432, 37.894882);
        createPost(members.get(3), "🏙️ #부산 #엘시티 다녀왔어요! 엘시티의 현대적이고 세련된 모습은 정말 압도적이었어요. 길을 걷다가도 고개가 절로 올라가더라구요. 특히, 전망대에서 바라본 해운대 해수욕장의 풍경은 정말 멋졌어요. 파란 바다와 끝없이 펼쳐진 해변이 한눈에 들어오는 그 느낌… 바다와 도시가 어우러지는 멋진 순간이었답니다! 하늘과 바다, 그리고 도시의 아름다움을 모두 즐기고 온 하루. 부산의 매력을 다시 한 번 느끼고 왔어요ㅎㅎ\n\n#부산여행 #엘시티뷰 #해운대 #부산핫플 #엘시티전망대 #부산바다 #해운대해수욕장 #바다와도시", "static/busan.jpeg", "static/busan1.jpeg", 129.132837, 35.160736);
        createPost(members.get(4), "🌴 #제주도 여행의 완벽한 하루! 먼저 더클리프에서 바다를 바라보며 여유롭게 시간을 보냈어요. 해변가를 전망하는 그 멋진 풍경은 정말 마음까지 힐링이었답니다ㅎㅎ 그리고 제주도에서 빼놓을 수 없는 국수바다! 정성껏 준비된 고기국수와 부드러운 편육 한 접시로 제주의 맛을 제대로 느꼈어요. 이 맛은 어디서도 못 느껴본 고소함과 깊은 풍미! 정말 제주도에서만 경험할 수 있는 특별한 맛이었어요.\n\n#제주여행 #더클리프 #국수바다 #해변전망 #고기국수 #편육 #제주도맛집 #힐링여행", "static/jejucafe.jpeg", "static/jejufood.jpeg", 126.560701, 33.499848);
    }

    // 게시글 생성
    private void createPost(Member member, String content, String imageFilePath1, String imageFilePath2, double mapX, double mapY) {
        PostRequest postRequest = PostRequest.builder()
                .mapX(mapX)
                .mapY(mapY)
                .content(content)
                .build();
        List<MultipartFile> imageFiles = List.of(
                getMultipartFileFromResource(imageFilePath1, imageFilePath1.substring(imageFilePath1.lastIndexOf("/") + 1)),
                getMultipartFileFromResource(imageFilePath2, imageFilePath2.substring(imageFilePath2.lastIndexOf("/") + 1))
        );
        postService.addPost(postRequest, imageFiles, memberConverter.toCurrentUserDto(member));
    }

    // 유저 3, 4, 5, 6이 모든 게시글에 대해 좋아요 누름
    @Transactional
    public void createPostLikes(List<Member> members) {
        List<Member> likingMembers = members.subList(2, members.size()); // 유저 3, 4, 5 (인덱스 2부터 시작)
        for (Member member : likingMembers) {
            for (long postId = 1L; postId <= 5L; postId++) {
                postService.addPostLike(postId, memberConverter.toCurrentUserDto(member)); // 게시글 좋아요 추가
            }
        }
    }

    // 모든 게시글에 서로 댓글&답글 작성
    private void createComments(List<Member> members) {
        commentUtils.createSampleCommentsForPosts(members);
    }

    // 유저 1이 1:1 채팅방 생성 (대상: 유저 2)
    private void createDmChatRoom(List<Member> members) {
        ChatRoomRequest dmChatRoomRequest = ChatRoomRequest.builder()
                .chatRoomMembers(List.of(
                        ChatRoomMember.builder().member(members.get(1)).build()
                ))
                .build();
        chatRoomService.addChatRoom(dmChatRoomRequest, memberConverter.toCurrentUserDto(members.get(0)));
    }

    // 유저 1이 단체 채팅방 생성 (대상: 유저 2, 3)
    private void createGroupChatRoom(List<Member> members) {
        ChatRoomRequest groupChatRoomRequest = ChatRoomRequest.builder()
                .title("테스트 채팅방")
                .chatRoomMembers(List.of(
                        ChatRoomMember.builder().member(members.get(1)).build(),
                        ChatRoomMember.builder().member(members.get(2)).build()
                ))
                .build();
        chatRoomService.addChatRoom(groupChatRoomRequest, memberConverter.toCurrentUserDto(members.get(0)));
    }

    // 유저 1, 2의 1:1 채팅
    private void createPrivateChatMessages(List<Member> members, Long chatRoomId) {
        // 유저1 -> 유저2 메시지
        chatMessageService.writeChatMessage(chatRoomId, "점심 먹었어?", members.get(0).getId());
        chatMessageService.readChatMessage(1L, members.get(1).getId()); // 유저2 읽음 처리

        // 유저2 -> 유저1 메시지
        chatMessageService.writeChatMessage(chatRoomId, "아직 안먹었는데 돈까스 ㄱㄱ?", members.get(1).getId());
    }

    // 유저 1, 2, 3의 단체 채팅
    private void createGroupChatMessages(List<Member> members, Long chatRoomId) {
        // 유저1 -> 유저2, 유저3 메시지
        chatMessageService.writeChatMessage(chatRoomId, "안녕하세요!", members.get(0).getId());
        chatMessageService.readChatMessage(3L, members.get(1).getId()); // 유저2 읽음 처리
        chatMessageService.readChatMessage(3L, members.get(2).getId()); // 유저3 읽음 처리

        // 유저2 -> 유저1, 유저3 메시지
        chatMessageService.writeChatMessage(chatRoomId, "안녕하세용", members.get(1).getId());
        chatMessageService.readChatMessage(4L, members.get(2).getId()); // 유저3 읽음 처리

        // 유저3 -> 유저1, 유저2 메시지
        chatMessageService.writeChatMessage(chatRoomId, "반가워요!!", members.get(2).getId());
        chatMessageService.readChatMessage(5L, members.get(1).getId()); // 유저2 읽음 처리
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

    // 스케줄러 수동 실행
    public void runDataFetchScheduler() {
        long startTotal = System.currentTimeMillis();

        long start = System.currentTimeMillis();
        dataFetchSchedule.fetchHotelDataSchedule();
        long fetchHotelTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        dataFetchSchedule.fetchRestaurantDataSchedule();
        long fetchRestaurantTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        dataFetchSchedule.addHotelCoordinateSchedule();
        long addHotelCoordinateTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        dataFetchSchedule.addRestaurantCoordinateSchedule();
        long addRestaurantCoordinateTime = System.currentTimeMillis() - start;

        long endTotal = System.currentTimeMillis();
        long totalTime = endTotal - startTotal;

        System.out.println("fetchHotelDataSchedule 실행 시간: " + formatDuration(fetchHotelTime));
        System.out.println("fetchRestaurantDataSchedule 실행 시간: " + formatDuration(fetchRestaurantTime));
        System.out.println("addHotelCoordinateSchedule 실행 시간: " + formatDuration(addHotelCoordinateTime));
        System.out.println("addRestaurantCoordinateSchedule 실행 시간: " + formatDuration(addRestaurantCoordinateTime));
        System.out.println("전체 스케줄러 실행 시간: " + formatDuration(totalTime));
    }

    // 진행시간 표시 형식
    private String formatDuration(long millis) {
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        long seconds = (millis % 60000) / 1000;
        long milliseconds = millis % 1000;
        return String.format("%02d시간 %02d분 %02d초 %03d밀리초", hours, minutes, seconds, milliseconds);
    }
}
