package com.deft.auth;

import com.deft.auth.service.AuthServiceRedis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sergey Golitsyn
 * created on 04.12.2023
 */
@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @MockBean
    private AuthServiceRedis authServiceRedis;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testExceptionHandlingUnauthorized() throws Exception {
        mockMvc.perform(get("/hello-world"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testExceptionHandling() throws Exception {
        List<GrantedAuthority> authorities = new ArrayList<>();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("token", "N/A", authorities);
        when(authServiceRedis.authenticate(any())).thenReturn(Optional.of(auth));
        mockMvc.perform(get("/login/qwe"))
                .andExpect(status().isNotFound());
    }
}
