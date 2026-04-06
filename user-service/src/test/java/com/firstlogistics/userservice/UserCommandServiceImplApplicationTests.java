package com.firstlogistics.userservice;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class UserCommandServiceImplApplicationTests {

    @MockitoBean
    private Keycloak keycloak;

    @Test
    void contextLoads() {
    }
}
