package com.letzgo.LetzgoBe.domain.account.oauth2.service;

import java.util.Map;

public interface OAuth2Service {
    // 소셜 로그인 리디렉션 URL 생성
    String getAuthUrl(String provider);

    // 소셜 로그인ㄹ
    Map<String, String> getUserInfo(String provider, String code);
}
