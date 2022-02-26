package com.api.bugtracker.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(BugNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public String bugNotFoundHandler(BugNotFoundException bnfe) {

        return bnfe.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public String projectNotFoundHandler(ProjectNotFoundException pnfe) {

        return pnfe.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public String userNotFoundHandler(UserNotFoundException unfe) {

        return unfe.getMessage();
    }
}
