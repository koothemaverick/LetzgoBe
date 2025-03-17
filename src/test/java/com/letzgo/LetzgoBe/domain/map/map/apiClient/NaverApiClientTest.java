package com.letzgo.LetzgoBe.domain.map.map.apiClient;

import com.google.maps.errors.ApiException;
import com.letzgo.LetzgoBe.domain.map.service.MapApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;

@SpringBootTest
class NaverApiClientTest {
    @Autowired
    public MapApiService mapApiService;
    @Test
    public void apiTest() throws IOException, InterruptedException, ApiException {
        System.out.println(mapApiService.getPlaceDetails("ChIJy9xwuUd2ezUREZpjyUeMW3I"));
    }
}