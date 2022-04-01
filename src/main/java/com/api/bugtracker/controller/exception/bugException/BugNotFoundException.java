package com.api.bugtracker.controller.exception.bugException;

public class BugNotFoundException extends RuntimeException {

    public BugNotFoundException(long id) {

        super("Could not find bug " + id);
    }
}
