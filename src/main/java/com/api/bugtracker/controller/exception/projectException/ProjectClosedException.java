package com.api.bugtracker.controller.exception.projectException;

public class ProjectClosedException extends RuntimeException {

    public ProjectClosedException(long id) {

        super("Project " + id + " is closed");
    }
}