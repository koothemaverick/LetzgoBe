package com.letzgo.LetzgoBe.global.initData;

import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberForm;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.domain.map.repository.PlaceRepository;
import com.letzgo.LetzgoBe.domain.map.repository.ReviewRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
@Profile("!prod")
public class NotProd {
    private final AuthService authService;
    private final MemberService memberService;
    private final ChatRoomService chatRoomService;


    public NotProd(AuthService authService, MemberService memberService, ChatRoomService chatRoomService,
                   PlaceRepository placeRepository, ReviewRepository reviewRepository) {
        this.authService = authService;
        this.memberService = memberService;
        this.chatRoomService = chatRoomService;
    }

    @Bean
    public ApplicationRunner applicationRunner(
    ){
        return new ApplicationRunner() {
            @Transactional
            @Override
            public void run(ApplicationArguments args) throws Exception {
                // 유저 1, 2, 3 생성
                MemberForm memberForm1 = MemberForm.builder()
                        .name("서울")
                        .nickname("seoul_gangnam")
                        .phone("010-1111-1111")
                        .email("user1@example.com")
                        .password("1234")
                        .gender(Member.Gender.MALE)
                        .birthday(LocalDate.of(1990, 1, 1))
                        .build();
                Member member1 = memberService.signup(memberForm1);

                MemberForm memberForm2 = MemberForm.builder()
                        .name("인천")
                        .nickname("incheon_songdo")
                        .phone("010-2222-2222")
                        .email("user2@example.com")
                        .password("1234")
                        .gender(Member.Gender.FEMALE)
                        .birthday(LocalDate.of(1992, 2, 2))
                        .build();
                Member member2 = memberService.signup(memberForm2);

                MemberForm memberForm3 = MemberForm.builder()
                        .name("강릉")
                        .nickname("gangneung_beach")
                        .phone("010-3333-3333")
                        .email("user3@example.com")
                        .password("1234")
                        .gender(Member.Gender.MALE)
                        .birthday(LocalDate.of(1994, 3, 3))
                        .build();
                Member member3 = memberService.signup(memberForm3);

/*
                // 유저 1, 2, 3, 4, 5 생성
                List<String> names = List.of("서울", "인천", "강릉", "부산", "제주");
                List<String> nicknames = List.of("seoul_gangnam", "incheon_songdo", "gangneung_beach", "busan_haeundae", "jeju_seaside");
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
                    Member member = memberService.signup(memberForm);
 */
                }


        };
    }
}
