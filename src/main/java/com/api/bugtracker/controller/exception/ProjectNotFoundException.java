package com.api.bugtracker.controller.exception;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(long id) {

        super("Could not find project " + id);
    }
}
