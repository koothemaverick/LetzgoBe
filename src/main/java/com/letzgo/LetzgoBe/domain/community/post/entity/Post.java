package com.letzgo.LetzgoBe.domain.community.post.entity;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Post extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostLike> likedMembers = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostSave> savedMembers = new ArrayList<>();

    @Column(length = 3000)
    private String content;

    private Double mapX;
    private Double mapY;

    @Type(JsonType.class) // Hibernate JSON 타입 적용
    @Column(columnDefinition = "jsonb") // PostgreSQL의 jsonb 타입 사용
    private List<String> imageUrls;
}
