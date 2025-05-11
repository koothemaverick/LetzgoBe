package com.letzgo.LetzgoBe.domain.dataFetcher.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class NcpGeocodeResponse {
    private List<Address> addresses;

    @Getter
    public static class Address {
        private String x; // 경도
        private String y; // 위도
    }
}