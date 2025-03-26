package com.letzgo.LetzgoBe.global.initData;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberForm;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.req.ChatRoomForm;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoomMember;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.service.ChatRoomService;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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

    public NotProd(AuthService authService, MemberService memberService, ChatRoomService chatRoomService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.chatRoomService = chatRoomService;
        this.memberRepository = memberRepository;
    }

    @Bean
    public ApplicationRunner applicationRunner(

    ){
        return new ApplicationRunner() {
            @Transactional
            @Override
            public void run(ApplicationArguments args) throws Exception {
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
                chatRoomService.addChatRoom(dmChatRoomForm, LoginUserDto.ConvertToLoginUserDto(members.get(0)));

                // 유저 1이 단체 채팅방 생성 (대상: 유저 2, 3)
                ChatRoomForm groupChatRoomForm = ChatRoomForm.builder()
                        .title("테스트 채팅방")
                        .chatRoomMembers(List.of(
                                ChatRoomMember.builder().member(members.get(1)).build(),
                                ChatRoomMember.builder().member(members.get(2)).build()
                        ))
                        .build();
                chatRoomService.addChatRoom(groupChatRoomForm, LoginUserDto.ConvertToLoginUserDto(members.get(0)));
            }
        };
    }
}
