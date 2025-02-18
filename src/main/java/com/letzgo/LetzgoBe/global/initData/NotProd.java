package com.letzgo.LetzgoBe.global.initData;

import com.letzgo.LetzgoBe.domain.account.auth.service.AuthService;
import com.letzgo.LetzgoBe.domain.account.user.dto.req.UserForm;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.service.UserService;
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
    private final UserService userService;

    public NotProd(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Bean
    public ApplicationRunner applicationRunner(

    ){
        return new ApplicationRunner() {
            @Transactional
            @Override
            public void run(ApplicationArguments args) throws Exception {
                // 유저 1, 2, 3 생성
                UserForm userForm1 = UserForm.builder()
                        .name("서울")
                        .nickName("seoul_gangnam")
                        .phone("010-1111-1111")
                        .email("user1@example.com")
                        .password("1234")
                        .gender(User.Gender.MALE)
                        .birthday(LocalDate.of(1990, 1, 1))
                        .build();
                userService.signup(userForm1);
                UserForm userForm2 = UserForm.builder()
                        .name("인천")
                        .nickName("incheon_songdo")
                        .phone("010-2222-2222")
                        .email("user2@example.com")
                        .password("1234")
                        .gender(User.Gender.FEMALE)
                        .birthday(LocalDate.of(1992, 2, 2))
                        .build();
                userService.signup(userForm2);
                UserForm userForm3 = UserForm.builder()
                        .name("강릉")
                        .nickName("gangneung_beach")
                        .phone("010-3333-3333")
                        .email("user3@example.com")
                        .password("1234")
                        .gender(User.Gender.MALE)
                        .birthday(LocalDate.of(1994, 3, 3))
                        .build();
                userService.signup(userForm3);

                
            }
        };
    }
}
