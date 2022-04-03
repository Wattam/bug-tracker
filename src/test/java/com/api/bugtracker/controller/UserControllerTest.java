package com.api.bugtracker.controller;

import com.api.bugtracker.controller.exception.userException.UserNotFoundException;
import com.api.bugtracker.model.User;
import com.api.bugtracker.service.UserService;
import com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

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

        Mockito.when(userService.all(0, 15)).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(page.getTotalElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].name", Matchers.is("name1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].username", Matchers.is("username1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].email", Matchers.is("email1@email1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].links.[0].rel", Matchers.is("self")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].links.[0].href", Matchers.is("http://localhost/users/1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].id", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].name", Matchers.is("name2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].username", Matchers.is("username2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].email", Matchers.is("email2@email2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].links.[0].rel", Matchers.is("self")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].links.[0].href", Matchers.is("http://localhost/users/2")));
    }

    @Test
    void shouldReturnOneUser() throws Exception {

        User user = new User(1L, "name", "username", "email@email", "password");

        Mockito.when(userService.one(Mockito.anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("username")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("email@email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.is("http://localhost/users/1")));
    }

    @Test
    void shouldNotReturnOneUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof UserNotFoundException)
                )
                .andExpect(result -> Assertions.assertEquals(
                        "Could not find user 1",
                        result.getResolvedException().getMessage())
                );
    }

    @Test
    void shouldCreateNewUser() throws Exception {

        User user = new User(1L, "name", "username", "email@email", "password");

        Mockito.when(userService.newUser(Mockito.any(User.class))).thenReturn(user);

        String json = new JSONObject()
                .put("name", "name")
                .put("username", "username")
                .put("email", "email@email")
                .put("password", "password")
                .toString();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("username")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("email@email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.is("http://localhost/users/1")));
    }

    @Test
    void shouldReplaceUser() throws Exception {

        User user = new User(1L, "name", "username", "email@email", "password");

        Mockito.when(userService.one(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userService.replaceUser(Mockito.any(User.class), Mockito.anyLong())).thenReturn(user);

        String json = new JSONObject()
                .put("name", "name")
                .put("username", "username")
                .put("email", "email@email")
                .put("password", "password")
                .toString();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("username")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("email@email")))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.is("http://localhost/users/1")));
    }

    @Test
    void shouldNotReplaceUser() throws Exception {

        String json = new JSONObject()
                .put("name", "name")
                .put("username", "username")
                .put("email", "email@email")
                .put("password", "password")
                .toString();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof UserNotFoundException)
                )
                .andExpect(result -> Assertions.assertEquals(
                        "Could not find user 1",
                        result.getResolvedException().getMessage())
                );
    }

    @Test
    void shouldDeleteUser() throws Exception {

        User user = new User();

        Mockito.when(userService.one(Mockito.anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void shouldNotDeleteUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof UserNotFoundException)
                )
                .andExpect(result -> Assertions.assertEquals(
                        "Could not find user 1",
                        result.getResolvedException().getMessage())
                );
    }
}