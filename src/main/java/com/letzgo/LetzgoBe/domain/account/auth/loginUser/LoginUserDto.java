package com.letzgo.LetzgoBe.domain.account.auth.loginUser;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.entity.UserFollow;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.entity.ChatRoom;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LoginUserDto {
    private Long id;
    private String name;
    private String nickName;
    private String phone;
    private String email;
    private String password;
    private User.Gender gender;
    private User.State state;
    private User.MemberRole role;
    private String profilePath;
    private List<ChatRoom> joinChatRoomList;
    private List<UserFollow> followUserList;
    private List<UserFollow> followerUserList;
    private LocalDate birthday;
    private LocalDateTime createDate;

    // User 객체를 LoginUserDto로 변환하는 정적 팩토리 메서드
    public static LoginUserDto from(User user) {
        return LoginUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .nickName(user.getNickName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .password(user.getPassword())
                .gender(user.getGender())
                .state(user.getState())
                .role(user.getRole())
                .profilePath(user.getProfilePath())
                .joinChatRoomList(user.getJoinChatRoomList())
                .followUserList(user.getFollowUserList())
                .followerUserList(user.getFollowerUserList())
                .birthday(user.getBirthday())
                .createDate(user.getCreateDate())
                .build();
    }

    // LoginUserDto를 User 엔티티로 변환하는 메서드
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .name(this.name)
                .nickName(this.nickName)
                .phone(this.phone)
                .email(this.email)
                .password(this.password)
                .gender(this.gender)
                .state(this.state)
                .role(this.role)
                .profilePath(this.profilePath)
                .joinChatRoomList(this.joinChatRoomList)
                .followUserList(this.followUserList)
                .followerUserList(this.followerUserList)
                .birthday(this.birthday)
                .createDate(this.createDate)
                .build();
    }
}
