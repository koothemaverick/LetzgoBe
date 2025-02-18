package com.letzgo.LetzgoBe.domain.account.auth.loginUser;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.entity.UserFollow;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.entity.ChatRoom;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LoginUserDto {
    private String name;
    private String nickname;
    private String phone;
    private String email;
    private String password;
    private User.Gender gender;
    private User.State state;
    private User.MemberRole role;
    private List<ChatRoom> joinChatRoomList;
    private List<UserFollow> followUserList;
    private List<UserFollow> followerUserList;
    private LocalDate birthday;

    public LoginUserDto(String name, String nickname, String phone, String email, String password, User.Gender gender,
                        User.State state, User.MemberRole role, List<ChatRoom> joinChatRoomList, List<UserFollow> followUserList,
                        List<UserFollow> followerUserList, LocalDate birthday) {
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.state = state;
        this.role = role;
        this.joinChatRoomList = joinChatRoomList;
        this.followUserList = followUserList;
        this.followerUserList = followerUserList;
        this.birthday = birthday;
    }
}
