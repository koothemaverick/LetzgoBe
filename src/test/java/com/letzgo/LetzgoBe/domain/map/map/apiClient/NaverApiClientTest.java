package com.letzgo.LetzgoBe.domain.map.map.apiClient;

import com.letzgo.LetzgoBe.domain.map.service.MapApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NaverApiClientTest {
    @Autowired
    public MapApiService mapApiService;
    @Test
    public void apiTest() {
        String s = mapApiService.googleApiTest("");
        System.out.println(s);
    }
}