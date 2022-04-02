package com.api.bugtracker.service.impl;

import java.util.Optional;

import com.api.bugtracker.model.User;
import com.api.bugtracker.repository.UserRepository;
import com.api.bugtracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public Page<User> all(int page, int size) {

        return repository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Optional<User> one(long id) {

        return repository.findById(id);
    }

    @Override
    public User newUser(User user) {

        return repository.save(user);
    }

    @Override
    public User replaceUser(User user, long id) {

        user.setId(id);
        return repository.save(user);
    }

    @Override
    public void deleteUser(long id) {

        repository.deleteById(id);
    }
}