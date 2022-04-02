package com.api.bugtracker.controller;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.api.bugtracker.controller.exception.userException.UserNotFoundException;
import com.api.bugtracker.model.Project;
import com.api.bugtracker.model.User;
import com.api.bugtracker.service.UserService;
import com.google.common.collect.ImmutableList;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {

        Mockito.reset(userService);
    }

    @Test
    void shouldReturnAllUsers() throws Exception {

        User user1 = new User(1L, "name1", "username1", "email1@email1", "password1");
        User user2 = new User(2L, "name2", "username2", "email2@email2", "password2");
        List<User> users = ImmutableList
                .<User>builder()
                .add(user1)
                .add(user2)
                .build();

        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 15), users.size());

        when(userService.all(0, 15)).thenReturn(page);

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(page.getTotalElements()))
                .andExpect(jsonPath("$.content.[0].id", is(1)))
                .andExpect(jsonPath("$.content.[0].name", is("name1")))
                .andExpect(jsonPath("$.content.[0].username", is("username1")))
                .andExpect(jsonPath("$.content.[0].email", is("email1@email1")))
                .andExpect(jsonPath("$.content.[0].links.[0].rel", is("self")))
                .andExpect(jsonPath("$.content.[0].links.[0].href", is("http://localhost/users/1")))
                .andExpect(jsonPath("$.content.[1].id", is(2)))
                .andExpect(jsonPath("$.content.[1].name", is("name2")))
                .andExpect(jsonPath("$.content.[1].username", is("username2")))
                .andExpect(jsonPath("$.content.[1].email", is("email2@email2")))
                .andExpect(jsonPath("$.content.[1].links.[0].rel", is("self")))
                .andExpect(jsonPath("$.content.[1].links.[0].href", is("http://localhost/users/2")));
    }

    @Test
    void shouldReturnOneUser() throws Exception {

        User user = new User(1L, "name", "username", "email@email", "password");

        when(userService.one(Mockito.anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.username", is("username")))
                .andExpect(jsonPath("$.email", is("email@email")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/users/1")));
    }

    @Test
    void shouldNotReturnOneUser() throws Exception {

        mockMvc.perform(get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("Could not find user 1",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldCreateNewUser() throws Exception {

        User user = new User(1L, "name", "username", "email@email", "password");

        when(userService.newUser(Mockito.any(User.class))).thenReturn(user);

        String json = new JSONObject()
                .put("name", "name")
                .put("username", "username")
                .put("email", "email@email")
                .put("password", "password")
                .toString();

        mockMvc.perform(
                        post("/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.username", is("username")))
                .andExpect(jsonPath("$.email", is("email@email")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/users/1")));
    }

    @Test
    void shouldReplaceUser() throws Exception {

        User user = new User(1L, "name", "username", "email@email", "password");

        when(userService.one(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(userService.replaceUser(Mockito.any(User.class), Mockito.anyLong())).thenReturn(user);

        String json = new JSONObject()
                .put("name", "name")
                .put("username", "username")
                .put("email", "email@email")
                .put("password", "password")
                .toString();

        mockMvc.perform(
                        put("/users/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.username", is("username")))
                .andExpect(jsonPath("$.email", is("email@email")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/users/1")));
    }

    @Test
    void shouldNotReplaceUser() throws Exception {

        String json = new JSONObject()
                .put("name", "name")
                .put("username", "username")
                .put("email", "email@email")
                .put("password", "password")
                .toString();

        mockMvc.perform(
                        put("/users/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("Could not find user 1",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldDeleteUser() throws Exception {

        User user = new User();

        when(userService.one(Mockito.anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteUser() throws Exception {

        mockMvc.perform(delete("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("Could not find user 1",
                        result.getResolvedException().getMessage()));
    }
}