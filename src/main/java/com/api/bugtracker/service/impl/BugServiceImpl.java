package com.api.bugtracker.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.api.bugtracker.model.Bug;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.repository.BugRepository;
import com.api.bugtracker.service.BugService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BugServiceImpl implements BugService {

    @Autowired
    private BugRepository repository;

    @Override
    public Page<Bug> all(int page, int size) {

        return repository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Optional<Bug> one(long id) {

        return repository.findById(id);
    }

    @Override
    public Bug newBug(Bug bug) {

        bug.setStatus(Status.OPEN);
        bug.setCreatedAt(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        return repository.save(bug);
    }

    @Override
    public Bug replaceBug(Bug bug, long id) {

        if (bug.getStatus().equals(Status.CLOSED) &&
                repository.findById(id).get().getStatus().equals(Status.OPEN)) {

            bug.setClosedAt(LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }

        bug.setUpdatedAt(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        bug.setId(id);
        return repository.save(bug);
    }

    @Override
    public void deleteBug(long id) {

        repository.deleteById(id);
    }

    @Override
    public void closeBug(long id) {

        Bug bug = repository.getById(id);
        bug.setStatus(Status.CLOSED);
        repository.save(bug);
    }
}