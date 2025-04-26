//package com.uzh.ase.dailygrind.userservice.userInfo.controller;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.uzh.ase.dailygrind.userservice.userInfo.repository.entity.User;
//import com.uzh.ase.dailygrind.userservice.userInfo.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//@WebMvcTest(value = UserController.class)
//@AutoConfigureMockMvc(addFilters = false)
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private UserService userService;
//
//    private User userInfo;
//
//    @BeforeEach
//    void setUp() {
//        userInfo = new User("1", "John Doe");
//    }
//
//    @Test
//    void testGetUserById_Success() throws Exception {
//        when(userService.getUserById("1")).thenReturn(userInfo);
//
//        mockMvc.perform(get("/users/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").value("1"))
//                .andExpect(jsonPath("$.name").value("John Doe"));
//
//        verify(userService, times(1)).getUserById("1");
//    }
//
//    @Test
//    void testGetUserById_NotFound() throws Exception {
//        when(userService.getUserById("1")).thenReturn(null);
//
//        mockMvc.perform(get("/users/1"))
//                .andExpect(status().isNotFound());
//
//        verify(userService, times(1)).getUserById("1");
//    }
//}
