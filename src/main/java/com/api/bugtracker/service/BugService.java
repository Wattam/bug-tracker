package com.api.bugtracker.service;

import java.util.List;
import java.util.Optional;

import com.api.bugtracker.model.Bug;

public interface BugService {

    public List<Bug> all();

    public Optional<Bug> one(long id);

    public Bug newBug(Bug bug);

    public Bug replaceBug(Bug bug, long id);

    public void deleteBug(long id);
}
