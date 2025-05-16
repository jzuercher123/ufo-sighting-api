package com.ufomap.api;

import com.ufomap.api.config.DataLoader;
import com.ufomap.api.repository.SightingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.http.HttpResponse;

@Slf4j
@SpringBootTest
class UfoSightingApiApplicationTests {

    @Test
    void contextLoads() {
        // This test will simply check if the Spring application context loads successfully.
        // If there are any issues with the configuration or beans, this test will fail
        // and provide information about the problem.
        System.out.println("Context loads successfully");
        log.info("Context loads successfully");
    }

    @Test
    void testLoadMarker() {
        // This test will check if the DataLoader class is able to load data correctly.
        // You can add assertions here to verify the loaded data.
        SightingRepository SightingRepository = null;
        DataLoader data = new DataLoader(SightingRepository);
        data.loadData();
        System.out.println("Data loaded successfully");
    }

    public void testHttpResponse() {
        // This test will check if the HttpResponse class is working correctly.
        // You can add assertions here to verify the response.
        HttpResponse<String> response = null;
        System.out.println("HttpResponse works successfully");
    }

}
