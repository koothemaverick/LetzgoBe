package com.letzgo.LetzgoBe.domain.account.user.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.user.dto.req.UserForm;
import com.letzgo.LetzgoBe.domain.account.user.dto.res.UserInfo;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.repository.UserRepository;
import com.letzgo.LetzgoBe.domain.account.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    // 회원가입
    @Override
    public void signup(UserForm userForm) {
        if (userRepository.existsByEmail((userForm.getEmail()))) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        User user = User.builder()
                        .name(userForm.getName())
                        .nickName(userForm.getNickName())
                        .phone(userForm.getPhone())
                        .email(userForm.getEmail())
                        .password(userForm.getPassword())
                        .gender(userForm.getGender())
                        .birthday(userForm.getBirthday())
                        .build();

        userRepository.save(user);
    }

    // 회원정보 조회
    @Override
    public UserInfo getUserInfo(String token, User loginUser) {
        return UserInfo.from(loginUser);
    }

    // 회원정보 수정
    @Override
    public void updateUser(UserForm userForm, String token, User loginUser) {
        loginUser.setPassword(BCrypt.hashpw(userForm.getPassword(), BCrypt.gensalt()));
        userRepository.save(loginUser);
    }

    // 회원탈퇴
    @Override
    public void deleteUser(String token, User loginUser) {
        userRepository.delete(loginUser);
    }
}
