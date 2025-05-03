package com.letzgo.LetzgoBe.global.initData;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberForm;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.req.ChatMessageForm;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.ChatRoomDto;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoom;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomMember;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.entity.Review;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.repository.ReviewRepository;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("!prod")
public class NotProd {
    private final MemberService memberService;
    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;
    private final ChatMessageService chatMessageService;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    public NotProd(AuthService authService,
                   MemberService memberService,
                   ChatRoomService chatRoomService,
                   MemberRepository memberRepository,
                   ChatMessageService chatMessageService,
                   PlaceRepository placeRepository,
                   ReviewRepository reviewRepository) {
        this.memberService = memberService;
        this.chatRoomService = chatRoomService;
        this.memberRepository = memberRepository;
        this.chatMessageService = chatMessageService;
        this.placeRepository = placeRepository;
        this.reviewRepository = reviewRepository;
    }

    @Bean
    public ApplicationRunner applicationRunner(

    ){
        return new ApplicationRunner() {
            @Transactional
            @Override
            public void run(ApplicationArguments args) {
                // 유저 1, 2, 3, 4, 5 생성
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
                    // Optional에서 Member로 변환
                    Member member = memberRepository.findByEmail(memberForm.getEmail())
                            .orElseThrow(() -> new ServiceException(ReturnCode.USER_NOT_FOUND));
                    members.add(member);
                }

                // 유저 1이 1:1 채팅방 생성 (대상: 유저 2)
                ChatRoomForm dmChatRoomForm = ChatRoomForm.builder()
                        .chatRoomMembers(List.of(
                                ChatRoomMember.builder().member(members.get(1)).build()
                        ))
                        .build();
                ChatRoomDto dmChatRoom = chatRoomService.addChatRoom(dmChatRoomForm, LoginUserDto.ConvertToLoginUserDto(members.get(0)));

                // 유저 1이 단체 채팅방 생성 (대상: 유저 2, 3)
                ChatRoomForm groupChatRoomForm = ChatRoomForm.builder()
                        .title("테스트 채팅방")
                        .chatRoomMembers(List.of(
                                ChatRoomMember.builder().member(members.get(1)).build(),
                                ChatRoomMember.builder().member(members.get(2)).build()
                        ))
                        .build();
                ChatRoomDto groupChatRoom = chatRoomService.addChatRoom(groupChatRoomForm, LoginUserDto.ConvertToLoginUserDto(members.get(0)));

                // 유저 1, 2의 1:1 채팅
                ChatMessageForm chatMessageForm1 = ChatMessageForm.builder()
                        .content("점심 먹었어?")
                        .build();
                chatMessageService.writeChatMessage(dmChatRoom.getId(), chatMessageForm1, LoginUserDto.ConvertToLoginUserDto(members.get(0)));

                ChatMessageForm chatMessageForm2 = ChatMessageForm.builder()
                        .content("아직 안먹었는데 돈까스 ㄱㄱ?")
                        .build();
                chatMessageService.writeChatMessage(dmChatRoom.getId(), chatMessageForm2, LoginUserDto.ConvertToLoginUserDto(members.get(1)));

                // 유저 1, 2, 3의 단체 채팅
                ChatMessageForm chatMessageForm3 = ChatMessageForm.builder()
                        .content("안녕하세요!")
                        .build();
                chatMessageService.writeChatMessage(groupChatRoom.getId(), chatMessageForm3, LoginUserDto.ConvertToLoginUserDto(members.get(0)));

                ChatMessageForm chatMessageForm4 = ChatMessageForm.builder()
                        .content("안녕하세용")
                        .build();
                chatMessageService.writeChatMessage(groupChatRoom.getId(), chatMessageForm4, LoginUserDto.ConvertToLoginUserDto(members.get(1)));

                ChatMessageForm chatMessageForm5 = ChatMessageForm.builder()
                        .content("반가워요!!")
                        .build();
               chatMessageService.writeChatMessage(groupChatRoom.getId(), chatMessageForm5, LoginUserDto.ConvertToLoginUserDto(members.get(2)));


                //장소데이터

                //인천대공원
                Place place1 = new Place("ChIJD3vd1Jh8ezURbh0AuKzX0Ig");
                //인천문학경기장
                Place place2 = new Place("ChIJQ_u_ZYF5ezURrffhNpjhTxk");
                //인천광역시청
                Place place3 = new Place("ChIJtYSzWuZ7ezURaJ9vi-a1UB8");
                //월미도
                Place place4 = new Place("ChIJbYc0JWOCezURypoST-McTYM");
                //센트럴파크
                Place place5 = new Place("ChIJvQNefoJ3ezURNM4TFnYUTjI");
                //인천 차이나타운
                Place place6 = new Place("ChIJ14998Jp4ezURyTMW5UI09Jc");
                //마시안해변
                Place place7 = new Place("ChIJxQc33uORezUR8hnBs0SauhA");
                //동화마을길
                Place place8 = new Place("ChIJraEdrJB4ezUR8w04ptlOUts");
                //소래습지 생태공원
                Place place9 = new Place("ChIJG0A3n-t7ezUR940fTAL73Rg");
                //옥토끼 우주센터
                Place place10 = new Place("ChIJv8ZRJQx5fDUR1-3A90_SkA0");
                //부평역 지하상가
                Place place11 = new Place("ChIJi9mzC0t8ezURlI-t4_nvB7s");

                List<Place> places = List.of(place1, place2, place3, place4, place5, place6, place7, place8, place9, place10, place11);
                placeRepository.saveAll(places);


                //리뷰데이터
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
        };
    }
}
