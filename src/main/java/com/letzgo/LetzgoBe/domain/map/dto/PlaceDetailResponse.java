package com.letzgo.LetzgoBe.domain.map.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlaceDetailResponse {
    String placeName;
    String address;
    String photo; //photo api에 사진 요청하는데 필요한 스트링
}
