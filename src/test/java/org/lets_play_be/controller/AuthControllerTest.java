package org.lets_play_be.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.ValidationErrorResponse;
import org.lets_play_be.exception.Violation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    EntityManager em;

    static ObjectMapper mapper = new ObjectMapper();


    @Nested
    @DisplayName("POST/api/v1/auth/login: ")
    class POSTLogin {

        @Test
        void whenClientLoginSuccess_Ok() throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{" +
                                            "\"email\": \"pavel@testemail.com\"," +
                                            "\"password\": \"User@Test1\"" +
                                            "}"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.accessTokenExpiration").exists())
                    .andExpect(header().exists("Set-Cookie"))
                    .andExpect(cookie().exists("access-token"))
                    .andExpect(cookie().exists("refresh-token"))
                    .andReturn();
        }

        @Test
        void whenLoginFailed_UNAUTHORIZED() throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{" +
                                            "\"email\": \"falseuser@testemail.com\"," +
                                            "\"password\": \"False!Password3\"" +
                                            "}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage").value("Bad credentials"))
                    .andExpect(cookie().doesNotExist("access-token"))
                    .andExpect(cookie().doesNotExist("refresh-token"))
                    .andReturn();
        }

        @Test
        void whenEmailAndPasswordInWrongFormat_BadRequest() throws Exception {

            Violation violation1 = new Violation("email", "Field email must be a valid email address");
            Violation violation2 = new Violation("password", "Password is given in wrong format");
            List<Violation> violations = new ArrayList<>(List.of(violation1, violation2));

            ValidationErrorResponse expectedResponse = getExpextedValidationErrorResponse(violations);

            MvcResult result = mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{" +
                                            "\"email\": \"falseusertestemail.com\"," +
                                            "\"password\": \"password\"" +
                                            "}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors.size()").value(2))
                    .andExpect(cookie().doesNotExist("access-token"))
                    .andExpect(cookie().doesNotExist("refresh-token"))
                    .andReturn();

            ValidationErrorResponse actualSortedResponse = getActualSortedResponse(result);

            assertThat(actualSortedResponse).isEqualTo(expectedResponse);
        }
    }


    @Nested
    @DisplayName("POST/api/v1/auth/register: ")
    class PostRegister {

        @Test
        void whenNewUserWasRegistered_WithAvatarUrl_Ok() throws Exception {

            MvcResult result = mockMvc.perform(
                            post("/api/v1/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{" +
                                            "\"name\":\"NewName\"," +
                                            "\"email\": \"newemail@testemail.com\"," +
                                            "\"password\": \"@Password1\"," +
                                            "\"avatarUrl\":\"someUrl\"" +
                                            "}")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(cookie().doesNotExist("access-token"))
                    .andReturn();

            AppUserFullResponse actualResponse = parseUserResponseFromMvcResult(result);

            AppUser savedUser = getUserFromByEmail("newemail@testemail.com");

            AppUserFullResponse expectedResponse = new AppUserFullResponse(savedUser);

            assertThat(actualResponse.userId()).isEqualTo(expectedResponse.userId());
            assertThat(actualResponse.name()).isEqualTo(expectedResponse.name());
            assertThat(actualResponse.email()).isEqualTo(expectedResponse.email());
            assertThat(actualResponse.avatarUrl()).isEqualTo(expectedResponse.avatarUrl());
            assertThat(actualResponse.roles()).isEqualTo(expectedResponse.roles());
            assertThat(actualResponse.availability()).isEqualTo(expectedResponse.availability());
            assertThat(actualResponse.fromAvailable()).isEqualTo(expectedResponse.fromAvailable());
            assertThat(actualResponse.toAvailable()).isEqualTo(expectedResponse.toAvailable());
        }

        @Test
        void whenNewUserWasRegistered_WithoutAvatarUrl_Ok() throws Exception {

            MvcResult result = mockMvc.perform(
                            post("/api/v1/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{" +
                                            "\"name\":\"NewName2\"," +
                                            "\"email\": \"newemail2@testemail.com\"," +
                                            "\"password\": \"@Password1\"," +
                                            "\"avatarUrl\":\"\"" +
                                            "}")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(cookie().doesNotExist("access-token"))
                    .andReturn();

            AppUserFullResponse actualResponse = parseUserResponseFromMvcResult(result);

            AppUser savedUser = getUserFromByEmail("newemail2@testemail.com");

            AppUserFullResponse expectedResponse = new AppUserFullResponse(savedUser);

            assertThat(actualResponse.userId()).isEqualTo(expectedResponse.userId());
            assertThat(actualResponse.name()).isEqualTo(expectedResponse.name());
            assertThat(actualResponse.email()).isEqualTo(expectedResponse.email());
            assertThat(actualResponse.avatarUrl()).isEqualTo(expectedResponse.avatarUrl());
            assertThat(actualResponse.roles()).isEqualTo(expectedResponse.roles());
            assertThat(actualResponse.availability()).isEqualTo(expectedResponse.availability());
            assertThat(actualResponse.fromAvailable()).isEqualTo(expectedResponse.fromAvailable());
            assertThat(actualResponse.toAvailable()).isEqualTo(expectedResponse.toAvailable());
        }

        @Test
        void whenNewUserWasRegistration_RequestFieldsValidationFailed_FieldsAreEmpty_BadRequest() throws Exception {

            Violation violation1 = new Violation("email", "Email should be in proper format");
            Violation violation2 = new Violation("email", "Email should not be empty");
            Violation violation3 = new Violation("name", "Name should not be shorter than 3 symbol");
            Violation violation4 = new Violation("password", "Password is given in wrong format");

            List<Violation> violations = new ArrayList<>(List.of(violation1, violation2, violation3, violation4));

            ValidationErrorResponse expectedResponse = getExpextedValidationErrorResponse(violations);

            MvcResult result = mockMvc.perform(
                            post("/api/v1/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{" +
                                            "\"name\":\"\"," +
                                            "\"email\": \"\"," +
                                            "\"password\": \" \"," +
                                            "\"avatarUrl\":\"\"" +
                                            "}")
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.size()").value(4))
                    .andReturn();

            ValidationErrorResponse actualSortedResponse = getActualSortedResponse(result);

            assertThat(actualSortedResponse).isEqualTo(expectedResponse);
        }
    }

//
//
//    @Test
//    void refreshAccessToken() {
//
//    }

    private AppUser getUserFromByEmail(String email) {
        return (AppUser) em.createQuery("SELECT u FROM AppUser as u WHERE u.email = ?1")
                .setParameter(1, email)
                .getSingleResult();
    }

    private ValidationErrorResponse getActualSortedResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {

        String json = result.getResponse().getContentAsString();
        ValidationErrorResponse response = mapper.readValue(json, ValidationErrorResponse.class);

        return getValidationErrorResponse(response.getErrors());
    }

    private AppUserFullResponse parseUserResponseFromMvcResult(MvcResult result) throws JsonProcessingException, UnsupportedEncodingException {

        String json = result.getResponse().getContentAsString();

        return mapper.readValue(json, AppUserFullResponse.class);
    }

    private static ValidationErrorResponse getExpextedValidationErrorResponse(List<Violation> violations) {

        sortViolations(violations);

        return getValidationErrorResponse(violations);
    }

    private static ValidationErrorResponse getValidationErrorResponse(List<Violation> violations) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        sortViolations(violations);
        response.setErrors(violations);
        return response;
    }

    private static void sortViolations(List<Violation> violations) {
        violations.sort(Comparator.comparing(Violation::field).thenComparing(Violation::message));
    }
}