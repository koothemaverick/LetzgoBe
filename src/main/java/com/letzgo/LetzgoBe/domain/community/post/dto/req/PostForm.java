package com.letzgo.LetzgoBe.domain.community.post.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostForm {
    private Double mapX;
    private Double mapY;
    private String content;
}
