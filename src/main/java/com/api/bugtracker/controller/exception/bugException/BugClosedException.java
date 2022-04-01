package com.api.bugtracker.controller.exception.bugException;

public class BugClosedException extends RuntimeException {

    public BugClosedException(long id) {

        super("Bug " + id + " is closed");
    }
}