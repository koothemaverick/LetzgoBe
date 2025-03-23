package com.letzgo.LetzgoBe.domain.community.post.dto.req;

import lombok.Data;

@Data
public class XYForm {
    private Double mapX; // longitude(경도)
    private Double mapY; // latitude(위도)
    private Integer radius; // 반경 값 (미터 단위)
}
