package com.letzgo.LetzgoBe.domain.map.dto.res;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlaceInfoResponse {
    private PlaceResponse placeinfo;
    @Builder.Default
    private List<ReviewResponse> reviews = new ArrayList<>();

}
