package com.finance.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.tracker.dto.request.CategoryRequest;
import com.finance.tracker.entity.User;
import com.finance.tracker.enums.Role;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = User.builder()
                .name("Test User")
                .email("test@finance.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.ADMIN)
                .build();
        userRepository.save(user);
        token = "Bearer " + jwtUtil.generateToken(user.getEmail());
    }

    @Test
    void shouldCreateCategory() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("New Category")
                .budgetLimit(new BigDecimal("1000"))
                .build();

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("New Category"));
    }

    @Test
    void shouldListCategories() throws Exception {
        mockMvc.perform(get("/api/categories")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldReturn401WhenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }
}
