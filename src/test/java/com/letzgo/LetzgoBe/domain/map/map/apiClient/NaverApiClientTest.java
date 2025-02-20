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
        String s = mapApiService.doReverseGeocoding("128.12345,37.98776");
        System.out.println(s);
    }
}