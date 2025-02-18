package com.letzgo.LetzgoBe.domain.account.auth.loginUser;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) != null &&
                parameter.getParameterType().equals(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails securityUser = (UserDetails) authentication.getPrincipal();
            String email = securityUser.getUsername(); // UserDetails의 getUsername()은 이메일을 반환

            // 이메일을 통해 User 엔터티 조회
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // User 엔터티를 기반으로 LoginUser DTO 생성
                return new LoginUserDto(user.getName(), user.getNickName(), user.getPhone(),user.getEmail(), user.getPassword(),
                        user.getGender(), user.getState(), user.getRole(), user.getJoinChatRoomList(), user.getFollowUserList(),
                        user.getFollowerUserList(), user.getBirthday());
            } else {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
        }
        return null;
    }
}
