package com.i2.kproject_2025_2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i2.kproject_2025_2.dto.LoginRequest;
import com.i2.kproject_2025_2.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("test@test.com", "password");
        String expectedToken = "test-jwt-token";
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedToken);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));
    }
}
