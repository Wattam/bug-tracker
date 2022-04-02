package com.api.bugtracker.service;

import java.util.Optional;

import com.api.bugtracker.model.Bug;
import org.springframework.data.domain.Page;

public interface BugService {

    Page<Bug> all(int page, int size);

    Optional<Bug> one(long id);

    Bug newBug(Bug bug);

    Bug replaceBug(Bug bug, long id);

    void deleteBug(long id);

    void closeBug(long id);
}