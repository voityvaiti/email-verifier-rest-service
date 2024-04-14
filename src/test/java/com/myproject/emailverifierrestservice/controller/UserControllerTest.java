package com.myproject.emailverifierrestservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.emailverifierrestservice.dto.UserResponseDto;
import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.service.abstraction.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Value("${api-prefix}")
    private String apiPrefix;


    private static final String email = "test@example.com";
    private static final AppUser user = new AppUser();
    private static final Authentication authentication = mock(Authentication.class);
    private static Page<AppUser> userPage;


    @BeforeAll
    static void init() {
        user.setEmail(email);

        userPage = new PageImpl<>(List.of(
                user, new AppUser(), new AppUser()
        ));

        when(authentication.getName()).thenReturn(email);
    }


    @Test
    @WithMockUser(username = email)
    void getCurrentUser_shouldReturnOkStatus() throws Exception {

        when(userService.getByEmail(anyString())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/user/current-user")
                        .principal(authentication))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = email)
    void getCurrentUser_shouldGetUserByProperUsername() throws Exception {

        when(userService.getByEmail(anyString())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/user/current-user")
                        .principal(authentication));

        verify(userService).getByEmail(email);
    }

    @Test
    @WithMockUser(username = email)
    void getCurrentUser_shouldReturnProperUserDto() throws Exception {

        UserResponseDto expectedResponseDto = new UserResponseDto();
        expectedResponseDto.setEmail(user.getEmail());

        when(userService.getByEmail(anyString())).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/user/current-user")
                        .principal(authentication))
                .andReturn();


        String responseBody = mvcResult.getResponse().getContentAsString();
        UserResponseDto actualResponseDto = new ObjectMapper().readValue(responseBody, UserResponseDto.class);
        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getUserPage_shouldReturnOkStatus() throws Exception {

        when(userService.getAll(any(PageRequest.class))).thenReturn(userPage);

        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/user/all"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getUserPage_shouldDoProperPageRequest() throws Exception {

        int pageNumber = 2;
        int pageSize = 10;

        when(userService.getAll(any(PageRequest.class))).thenReturn(userPage);

        mockMvc.perform(MockMvcRequestBuilders.get(apiPrefix + "/user/all")
                        .param("page-number", String.valueOf(pageNumber))
                        .param("page-size", String.valueOf(pageSize)));

        verify(userService).getAll(PageRequest.of(pageNumber, pageSize));
    }
}