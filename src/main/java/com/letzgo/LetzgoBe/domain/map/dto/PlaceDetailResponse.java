package com.letzgo.LetzgoBe.domain.map.dto;

import com.google.maps.model.Photo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlaceDetailResponse {
    String placeName;
    String address;
    Photo[] photos; //photo api에 사진 요청하는데 필요한 스트링들
}
