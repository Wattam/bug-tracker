package com.api.bugtracker.service;

import java.util.Optional;

import com.api.bugtracker.model.Project;
import org.springframework.data.domain.Page;

public interface ProjectService {

    Page<Project> all(int page, int size);

    Optional<Project> one(long id);

    Project newProject(Project project);

    Project replaceProject(Project project, long id);

    void deleteProject(long id);

    void closeProject(long id);
}