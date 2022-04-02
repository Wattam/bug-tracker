package com.api.bugtracker.service.impl;

import java.util.Optional;

import com.api.bugtracker.model.Project;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.repository.ProjectRepository;
import com.api.bugtracker.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Override
    public Page<Project> all(int page, int size) {

        return repository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Optional<Project> one(long id) {

        return repository.findById(id);
    }

    @Override
    public Project newProject(Project project) {

        project.setStatus(Status.OPEN);
        return repository.save(project);
    }

    @Override
    public Project replaceProject(Project project, long id) {

        project.setId(id);
        return repository.save(project);
    }

    @Override
    public void deleteProject(long id) {

        repository.deleteById(id);
    }

    @Override
    public void closeProject(long id) {

        Project project = repository.getById(id);
        project.setStatus(Status.CLOSED);
        repository.save(project);
    }
}