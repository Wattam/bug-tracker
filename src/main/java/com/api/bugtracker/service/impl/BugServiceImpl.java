package com.api.bugtracker.service.impl;

import java.util.List;
import java.util.Optional;

import com.api.bugtracker.model.Bug;
import com.api.bugtracker.repository.BugRepository;
import com.api.bugtracker.service.BugService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BugServiceImpl implements BugService {

    @Autowired
    private BugRepository repository;

    @Override
    public List<Bug> all() {

        return repository.findAll();
    }

    @Override
    public Optional<Bug> one(long id) {

        return repository.findById(id);
    }

    @Override
    public Bug newBug(Bug bug) {

        return repository.save(bug);
    }

    @Override
    public Bug replaceBug(Bug bug, long id) {

        bug.setId(id);
        return repository.save(bug);
    }

    @Override
    public void deleteBug(long id) {

        repository.deleteById(id);
    }
}
