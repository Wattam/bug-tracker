package com.api.bugtracker.service;

import java.util.Optional;

import com.api.bugtracker.model.User;
import org.springframework.data.domain.Page;

public interface UserService {

    Page<User> all(int page, int size);

    Optional<User> one(long id);

    User newUser(User user);

    User replaceUser(User user, long id);

    void deleteUser(long id);
}