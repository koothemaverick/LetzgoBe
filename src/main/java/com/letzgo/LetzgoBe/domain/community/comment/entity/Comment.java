package com.letzgo.LetzgoBe.domain.community.comment.entity;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.community.post.entity.Post;
import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Comment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommentLike> likedMembers = new ArrayList<>();

    @Column(length = 1000)
    @Size(max = 1000)
    private String content;
    private Long superCommentId;
}
