package com.api.bugtracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.api.bugtracker.model.User;
import com.api.bugtracker.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    void shouldReturnAllUsers() {

        userRepository.save(new User(1L, "name1", "username1", "email1@email1", "password1"));
        userRepository.save(new User(2L, "name2", "username2", "email2@email2", "password2"));

        Page<User> users = userService.all(0, 15);

        assertTrue(users.hasContent());
        assertEquals(2, users.getTotalElements());
        assertEquals(2, users.getNumberOfElements());

        assertEquals(1L, users.getContent().get(0).getId());
        assertEquals("name1", users.getContent().get(0).getName());
        assertEquals("username1", users.getContent().get(0).getUsername());
        assertEquals("email1@email1", users.getContent().get(0).getEmail());
        assertEquals("password1", users.getContent().get(0).getPassword());

        assertEquals(2L, users.getContent().get(1).getId());
        assertEquals("name2", users.getContent().get(1).getName());
        assertEquals("username2", users.getContent().get(1).getUsername());
        assertEquals("email2@email2", users.getContent().get(1).getEmail());
        assertEquals("password2", users.getContent().get(1).getPassword());
    }

    @Test
    void shouldNotReturnAnyUser() {

        Page<User> users = userService.all(0, 15);

        assertTrue(users.isEmpty());
        assertEquals(0, users.getTotalElements());
        assertEquals(0, users.getNumberOfElements());
    }

    @Test
    @DirtiesContext
    void shouldReturnOneUser() {

        User expected = userRepository.save(new User(1L, "name", "username", "email@email", "password"));

        User actual = userService.one(1L).get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    void shouldNotReturnOneUser() {

        assertTrue(userService.one(1L).isEmpty());
        assertFalse(userService.one(1L).isPresent());
    }

    @Test
    @DirtiesContext
    void shouldCreateNewUser() {

        User expected = new User(1L, "name", "username", "email@email", "password");
        expected = userService.newUser(expected);

        User actual = userRepository.findById(expected.getId()).get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    @DirtiesContext
    void shouldReplaceUser() {

        User user = new User(1L, "name", "username", "email@email", "password");
        user = userRepository.save(user);

        User expected = new User(1L, "nameUpdated", "usernameUpdated", "email@emailUpdated", "passwordUpdated");

        userService.replaceUser(expected, user.getId());
        User actual = userRepository.findById(user.getId()).get();

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    @DirtiesContext
    void shouldDeleteUser() {

        User user = new User(1L, "name", "username", "email@email", "password");
        user = userRepository.save(user);

        userService.deleteUser(user.getId());

        assertTrue(userRepository.findById(user.getId()).isEmpty());
        assertFalse(userRepository.findById(user.getId()).isPresent());
    }
}