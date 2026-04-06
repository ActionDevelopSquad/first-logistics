package com.firstlogistics.hubservice;

import com.firstlogistics.hubservice.global.config.TestCacheConfig;
import com.firstlogistics.hubservice.hubconnection.infrastructure.feign.CompanyClient;
import com.firstlogistics.hubservice.hubconnection.infrastructure.feign.NaverMapClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestCacheConfig.class)
class HubServiceApplicationTests {

    @MockitoBean
    private CompanyClient companyClient;

    @MockitoBean
    private NaverMapClient naverMapClient;
    @Test
    void contextLoads() {
    }

}
