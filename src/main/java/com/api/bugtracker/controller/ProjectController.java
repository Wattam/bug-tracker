package com.api.bugtracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.api.bugtracker.controller.exception.ProjectNotFoundException;
import com.api.bugtracker.model.Project;
import com.api.bugtracker.component.ProjectModelAssembler;
import com.api.bugtracker.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectModelAssembler projectAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EntityModel<Project>> all() {

        return projectService.all().stream().map(projectAssembler::toModel).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Project> one(@PathVariable long id) {

        return projectAssembler.toModel(
                projectService.one(id).orElseThrow(() -> new ProjectNotFoundException(id)));
    }

    @PostMapping
    public ResponseEntity<?> newProject(@Valid @RequestBody Project project) {

        EntityModel<Project> projectEM = projectAssembler.toModel(
                projectService.newProject(project));

        return ResponseEntity
                .created(projectEM.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(projectEM);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceProject(@Valid @RequestBody Project project, @PathVariable long id) {

        projectService.one(id).orElseThrow(() -> new ProjectNotFoundException(id));

        EntityModel<Project> projectEM = projectAssembler.toModel(
                projectService.replaceProject(project, id));

        return ResponseEntity
                .created(projectEM.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(projectEM);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable long id) {

        projectService.one(id).orElseThrow(() -> new ProjectNotFoundException(id));

        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();
    }
}
