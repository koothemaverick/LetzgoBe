package com.letzgo.LetzgoBe.domain.map.config;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class googleApiClient {
    @Bean(destroyMethod = "shutdown")
    public GeoApiContext geoApiContext(@Value("${api-keys.google.client-key}") String apiKey) {
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .disableRetries()
                .build();
    }
}
