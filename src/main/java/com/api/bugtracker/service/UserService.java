package com.api.bugtracker.service;

import java.util.List;
import java.util.Optional;

import com.api.bugtracker.model.User;

public interface UserService {

    public List<User> all();

    public Optional<User> one(long id);

    public User newUser(User user);

    public User replaceUser(User user, long id);

    public void deleteUser(long id);
}
