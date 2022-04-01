package com.api.bugtracker.controller.exception.projectException;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(long id) {

        super("Could not find project " + id);
    }
}
