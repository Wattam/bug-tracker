package com.api.bugtracker.controller.exception;

import com.api.bugtracker.controller.exception.bugException.BugClosedException;
import com.api.bugtracker.controller.exception.bugException.BugNotFoundException;
import com.api.bugtracker.controller.exception.projectException.ProjectClosedException;
import com.api.bugtracker.controller.exception.projectException.ProjectNotFoundException;
import com.api.bugtracker.controller.exception.userException.UserNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(BugNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String bugNotFoundHandler(BugNotFoundException bnfe) {

        return bnfe.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String projectNotFoundHandler(ProjectNotFoundException pnfe) {

        return pnfe.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotFoundHandler(UserNotFoundException unfe) {

        return unfe.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(BugClosedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String bugClosedHandler(BugClosedException bce) {

        return bce.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ProjectClosedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String projectClosedHandler(ProjectClosedException pce) {

        return pce.getMessage();
    }
}
