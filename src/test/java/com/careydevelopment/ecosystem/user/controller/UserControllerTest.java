package com.careydevelopment.ecosystem.user.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.careydevelopment.ecosystem.user.harness.UserHarness;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.service.UserService;
import com.careydevelopment.ecosystem.user.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityUtil securityUtil;
    
    @MockBean
    private UserService userService;
    
    
    @Test
    public void testRetrieveMeGoodResponse() throws Exception {
        final User user = UserHarness.getValidUser();
        
        Mockito.when(securityUtil.getCurrentUser()).thenReturn(user);
        
        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/user/me"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(user.getId())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is(user.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", Matchers.is(user.getLastName())));
    }
    
    
    @Test
    public void testRetrieveMeNotFoundResponse() throws Exception {
        Mockito.when(securityUtil.getCurrentUser()).thenThrow(new RuntimeException("Intended exception"));
        
        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/user/me"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()));
    }
    
    
    @Test
    public void testUpdateUserGoodResponse() throws Exception {
        User updatedUser = UserHarness.getValidUser();
        updatedUser.setLastName("Schmong");
        
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedUserJson = objectMapper.writeValueAsString(updatedUser);

        Mockito.when(securityUtil.isAuthorizedByUserId(Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.updateUser(Mockito.any(User.class))).thenReturn(updatedUser);
        
        this.mockMvc
            .perform(MockMvcRequestBuilders.put("/user/" + updatedUser.getId())
            .content(updatedUserJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(updatedUser.getId())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", Matchers.is("Schmong")));
    }
    
    
    @Test
    public void testUpdateUserUnauthorizedResponse() throws Exception {
        User updatedUser = UserHarness.getValidUser();
        updatedUser.setLastName("Schmong");
        
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedUserJson = objectMapper.writeValueAsString(updatedUser);

        Mockito.when(securityUtil.isAuthorizedByUserId(Mockito.anyString())).thenReturn(false);
        Mockito.when(userService.updateUser(Mockito.any(User.class))).thenReturn(updatedUser);
        
        this.mockMvc
            .perform(MockMvcRequestBuilders.put("/user/" + updatedUser.getId())
            .content(updatedUserJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));
    }
}
