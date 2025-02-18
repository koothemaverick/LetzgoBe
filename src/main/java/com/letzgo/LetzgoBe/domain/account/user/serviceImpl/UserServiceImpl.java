package com.letzgo.LetzgoBe.domain.account.user.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.user.dto.req.UserForm;
import com.letzgo.LetzgoBe.domain.account.user.dto.res.UserInfo;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.repository.UserRepository;
import com.letzgo.LetzgoBe.domain.account.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    // 회원가입
    @Override
    @Transactional
    public void signup(UserForm userForm) {
        if (userRepository.existsByEmail((userForm.getEmail()))) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        String encodedPassword = passwordEncoder.encode(userForm.getPassword());
        User user = User.builder()
                        .name(userForm.getName())
                        .nickName(userForm.getNickName())
                        .phone(userForm.getPhone())
                        .email(userForm.getEmail())
                        .password(encodedPassword)  // 인코딩된 비밀번호 저장
                        .gender(userForm.getGender())
                        .birthday(userForm.getBirthday())
                        .build();

        userRepository.save(user);
    }

    // 회원정보 조회
    @Override
    @Transactional
    public UserInfo getUserInfo(LoginUserDto loginUser) {
        return UserInfo.from(loginUser);
    }

    // 회원정보 수정
    @Override
    @Transactional
    public void updateUser(UserForm userForm, LoginUserDto loginUser) {
        if (userForm.getName() != null) {
            loginUser.setName(userForm.getName());
        }
        if (userForm.getNickName() != null) {
            loginUser.setNickName(userForm.getNickName());
        }
        if (userForm.getPhone() != null) {
            loginUser.setPhone(userForm.getPhone());
        }
        if (userForm.getEmail() != null) {
            loginUser.setEmail(userForm.getEmail());
        }
        if (userForm.getPassword() != null) {
            loginUser.setPassword(BCrypt.hashpw(userForm.getPassword(), BCrypt.gensalt()));
        }
        if (userForm.getGender() != null) {
            loginUser.setGender(userForm.getGender());
        }
        if (userForm.getBirthday() != null) {
            loginUser.setBirthday(userForm.getBirthday());
        }
        // LoginUserDto를 User 엔티티로 변환
        User userEntity = loginUser.toEntity();
        userRepository.save(userEntity);
    }

    // 회원탈퇴
    @Override
    @Transactional
    public void deleteUser(LoginUserDto loginUser) {
        // refreshToken 삭제
        authService.logout(loginUser);
        // LoginUserDto를 User 엔티티로 변환
        User userEntity = loginUser.toEntity();
        userRepository.delete(userEntity);
    }
}
