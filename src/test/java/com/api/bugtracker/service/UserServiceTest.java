package com.api.bugtracker.service;

import com.api.bugtracker.model.User;
import com.api.bugtracker.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
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

        Assertions.assertTrue(users.hasContent());
        Assertions.assertEquals(2, users.getTotalElements());
        Assertions.assertEquals(2, users.getNumberOfElements());

        Assertions.assertEquals(1L, users.getContent().get(0).getId());
        Assertions.assertEquals("name1", users.getContent().get(0).getName());
        Assertions.assertEquals("username1", users.getContent().get(0).getUsername());
        Assertions.assertEquals("email1@email1", users.getContent().get(0).getEmail());
        Assertions.assertEquals("password1", users.getContent().get(0).getPassword());

        Assertions.assertEquals(2L, users.getContent().get(1).getId());
        Assertions.assertEquals("name2", users.getContent().get(1).getName());
        Assertions.assertEquals("username2", users.getContent().get(1).getUsername());
        Assertions.assertEquals("email2@email2", users.getContent().get(1).getEmail());
        Assertions.assertEquals("password2", users.getContent().get(1).getPassword());
    }

    @Test
    void shouldNotReturnAnyUser() {

        Page<User> users = userService.all(0, 15);

        Assertions.assertTrue(users.isEmpty());
        Assertions.assertEquals(0, users.getTotalElements());
        Assertions.assertEquals(0, users.getNumberOfElements());
    }

    @Test
    @DirtiesContext
    void shouldReturnOneUser() {

        User expected = userRepository.save(new User(1L, "name", "username", "email@email", "password"));

        User actual = userService.one(1L).get();

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getUsername(), actual.getUsername());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    void shouldNotReturnOneUser() {

        Assertions.assertTrue(userService.one(1L).isEmpty());
        Assertions.assertFalse(userService.one(1L).isPresent());
    }

    @Test
    @DirtiesContext
    void shouldCreateNewUser() {

        User expected = new User(1L, "name", "username", "email@email", "password");
        expected = userService.newUser(expected);

        User actual = userRepository.findById(expected.getId()).get();

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getUsername(), actual.getUsername());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    @DirtiesContext
    void shouldReplaceUser() {

        User user = new User(1L, "name", "username", "email@email", "password");
        user = userRepository.save(user);

        User expected = new User(1L, "nameUpdated", "usernameUpdated", "email@emailUpdated", "passwordUpdated");

        userService.replaceUser(expected, user.getId());
        User actual = userRepository.findById(user.getId()).get();

        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getUsername(), actual.getUsername());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    @DirtiesContext
    void shouldDeleteUser() {

        User user = new User(1L, "name", "username", "email@email", "password");
        user = userRepository.save(user);

        userService.deleteUser(user.getId());

        Assertions.assertTrue(userRepository.findById(user.getId()).isEmpty());
        Assertions.assertFalse(userRepository.findById(user.getId()).isPresent());
    }
}