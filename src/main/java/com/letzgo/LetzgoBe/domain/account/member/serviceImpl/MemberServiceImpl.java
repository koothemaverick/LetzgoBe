package com.letzgo.LetzgoBe.domain.account.member.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.member.dto.req.MemberForm;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.DetailMemberInfo;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.MemberInfo;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.repository.MemberRepository;
import com.letzgo.LetzgoBe.domain.account.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    // 회원가입
    @Override
    @Transactional
    public Member signup(MemberForm memberForm) {
        if (memberRepository.existsByEmail((memberForm.getEmail()))) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        String encodedPassword = passwordEncoder.encode(memberForm.getPassword());
        Member member = Member.builder()
                        .name(memberForm.getName())
                        .nickname(memberForm.getNickname())
                        .phone(memberForm.getPhone())
                        .email(memberForm.getEmail())
                        .password(encodedPassword)  // 인코딩된 비밀번호 저장
                        .gender(memberForm.getGender())
                        .birthday(memberForm.getBirthday())
                        .build();

        memberRepository.save(member);
        return member;
    }

    // 회원정보 조회
    @Override
    @Transactional
    public MemberInfo getMemberInfo(LoginUserDto loginUser) {
        return convertToMemberInfo(loginUser);
    }

    // 회원정보 수정
    @Override
    @Transactional
    public void updateMember(MemberForm memberForm, LoginUserDto loginUser) {
        if (memberForm.getName() != null) {
            loginUser.setName(memberForm.getName());
        }
        if (memberForm.getNickname() != null) {
            loginUser.setNickname(memberForm.getNickname());
        }
        if (memberForm.getPhone() != null) {
            loginUser.setPhone(memberForm.getPhone());
        }
        if (memberForm.getEmail() != null) {
            loginUser.setEmail(memberForm.getEmail());
        }
        if (memberForm.getPassword() != null) {
            loginUser.setPassword(BCrypt.hashpw(memberForm.getPassword(), BCrypt.gensalt()));
        }
        if (memberForm.getGender() != null) {
            loginUser.setGender(memberForm.getGender());
        }
        if (memberForm.getBirthday() != null) {
            loginUser.setBirthday(memberForm.getBirthday());
        }
        // LoginUserDto를 Member 엔티티로 변환
        Member memberEntity = loginUser.ConvertToMember();
        memberRepository.save(memberEntity);
    }

    // 회원탈퇴
    @Override
    @Transactional
    public void deleteMember(LoginUserDto loginUser) {
        // refreshToken 삭제
        authService.logout(loginUser);
        // LoginUserDto를 User 엔티티로 변환
        Member memberEntity = loginUser.ConvertToMember();
        memberRepository.delete(memberEntity);
    }

    // LoginUser를 MemberInfo로 변환
    private MemberInfo convertToMemberInfo(LoginUserDto loginUser) {
        return MemberInfo.builder()
                .id(loginUser.getId())
                .name(loginUser.getName())
                .nickName(loginUser.getNickname())
                .profileImgUrl(loginUser.getProfileImageUrl())
                .followMemberCount(loginUser.getFollowList().stream().count())
                .followedMemberCount(loginUser.getFollowedList().stream().count())
                .build();
    }

    // LoginUser를 DetailMemberInfo로 변환
    private DetailMemberInfo convertToDetailMemberInfo(LoginUserDto loginUser) {
        return DetailMemberInfo.builder()
                .id(loginUser.getId())
                .name(loginUser.getName())
                .nickName(loginUser.getNickname())
                .phone(loginUser.getPhone())
                .email(loginUser.getEmail())
                .gender(loginUser.getGender())
                .birthday(loginUser.getBirthday())
                .profileImgUrl(loginUser.getProfileImageUrl())
                .followMemberCount(loginUser.getFollowList().stream().count())
                .followedMemberCount(loginUser.getFollowedList().stream().count())
                .build();
    }
}
