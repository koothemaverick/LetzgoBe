package com.letzgo.LetzgoBe.domain.community.post.entity;

import lombok.Data;
import lombok.Getter;

@Data
public class PostPage {
    // 기본 page, size
    private int page = 0;
    private int size = 24;
    @Getter
    private static final int maxPageSize = 24;
}
