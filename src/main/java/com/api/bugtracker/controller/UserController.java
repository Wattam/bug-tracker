package com.api.bugtracker.controller;

import javax.validation.Valid;

import com.api.bugtracker.component.UserModelAssembler;
import com.api.bugtracker.controller.exception.userException.UserNotFoundException;
import com.api.bugtracker.model.User;
import com.api.bugtracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserModelAssembler userAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EntityModel<User>> all(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size
    ) {

        return userService.all(page, size).map(userAssembler::toModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<User>> one(@PathVariable long id) {

        return ResponseEntity.ok(
                userAssembler.toModel(
                        userService.one(id).orElseThrow(() -> new UserNotFoundException(id))
                )
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<User>> newUser(@Valid @RequestBody User user) {

        EntityModel<User> userEM = userAssembler.toModel(
                userService.newUser(user));

        return ResponseEntity
                .created(userEM.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(userEM);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<User>> replaceUser(@Valid @RequestBody User user, @PathVariable long id) {

        userService.one(id).orElseThrow(() -> new UserNotFoundException(id));

        EntityModel<User> userEM = userAssembler.toModel(
                userService.replaceUser(user, id));

        return ResponseEntity
                .created(userEM.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(userEM);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {

        userService.one(id).orElseThrow(() -> new UserNotFoundException(id));

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}