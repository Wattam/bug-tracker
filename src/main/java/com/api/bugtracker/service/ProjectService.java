package com.api.bugtracker.service;

import java.util.List;
import java.util.Optional;

import com.api.bugtracker.model.Project;

public interface ProjectService {

    public List<Project> all();

    public Optional<Project> one(long id);

    public Project newProject(Project project);

    public Project replaceProject(Project project, long id);

    public void deleteProject(long id);

    public void closeProject(long id);
}
