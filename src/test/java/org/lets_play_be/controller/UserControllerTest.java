package org.lets_play_be.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    UserController userController;
    @Autowired
    MockMvc mockMvc;

    @Test
    void getUserData_NotAuthorized() throws Exception {
        mockMvc.perform(get("/api/v1/invite/user"))
                .andExpect(status().isUnauthorized());

    }

    @Sql(scripts = "/sql/app_user_init.sql")
    @WithMockUser(username = "email1@email.com", roles = "ROLE_ADMIN")
    @Test
    void getUserData() throws Exception {
        mockMvc.perform(get("/api/v1/invite/user"))
                .andExpect(status().isOk());

    }

    @Test
    void updateUserData() {
    }

    @Test
    void updateUserAvailability() {
    }
}