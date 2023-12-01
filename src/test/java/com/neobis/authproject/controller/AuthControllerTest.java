package com.neobis.authproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobis.authproject.entity.User;
import com.neobis.authproject.entity.dto.request.LoginRequest;
import com.neobis.authproject.entity.dto.request.RegistrationRequest;
import com.neobis.authproject.entity.enums.Role;
import com.neobis.authproject.entity.enums.UserState;
import com.neobis.authproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class AuthControllerTest {

    String URL = "/api/auth";

    MockMvc mockMvc;
    ObjectMapper mapper;
    UserRepository userRepository;
    BCryptPasswordEncoder encoder;

    @Autowired
    public AuthControllerTest(ObjectMapper mapper, WebApplicationContext webApplicationContext,
                              UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.mapper = mapper;
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Test
    void registration() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(URL + "/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(RegistrationRequest.builder()
                                .email("notexist@mail.com")
                                .password("regis_password")
                                .username("regis_username")
                                .build())))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    void sendMessage() throws Exception {
        if (userRepository.findByEmail("notexist@mail.com").isEmpty()) {
            userRepository.save(User.builder()
                    .email("notexist@mail.com")
                    .role(Role.USER)
                    .UUIDExpirationDate(LocalDateTime.now().plusMinutes(5))
                    .state(UserState.DISABLED)
                    .UUID("cool_token")
                    .username("regis_username")
                    .build());
        }
        this.mockMvc.perform(MockMvcRequestBuilders.put(URL + "/send-message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("link", "some_link")
                        .content(mapper.writeValueAsString(RegistrationRequest.builder()
                                .email("notexist@mail.com")
                                .password("regis_password")
                                .username("regis_username")
                                .build())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void ensureRegistration() throws Exception{
        if (userRepository.findByUUID("cool_token").isEmpty()) {
            userRepository.save(User.builder()
                            .role(Role.USER)
                            .UUIDExpirationDate(LocalDateTime.now().plusMinutes(5))
                            .state(UserState.DISABLED)
                            .UUID("cool_token")
                    .build());
        }
        this.mockMvc.perform(MockMvcRequestBuilders.put(URL + "/ensure-registration")
                        .param("token", "cool_token"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void login() throws Exception{
        if (userRepository.findByUsername("some_username_valid").isEmpty()) {
            userRepository.save(User.builder()
                    .role(Role.USER)
                    .UUIDExpirationDate(LocalDateTime.now().plusMinutes(5))
                    .state(UserState.ACTIVATED)
                    .username("some_username")
                    .password(encoder.encode("some_password"))
                    .build());
        }
        this.mockMvc.perform(MockMvcRequestBuilders.post(URL + "/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(LoginRequest.builder()
                                .password("some_password")
                                .username("some_username")
                                .build())))
                .andDo(print())
                .andExpect(status().isOk());
    }
}