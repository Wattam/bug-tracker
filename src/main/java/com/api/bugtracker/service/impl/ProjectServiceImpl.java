package com.api.bugtracker.service.impl;

import java.util.List;
import java.util.Optional;

import com.api.bugtracker.model.Project;
import com.api.bugtracker.repository.ProjectRepository;
import com.api.bugtracker.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Override
    public List<Project> all() {

        return repository.findAll();
    }

    @Override
    public Optional<Project> one(long id) {

        return repository.findById(id);
    }

    @Override
    public Project newProject(Project project) {

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
}
