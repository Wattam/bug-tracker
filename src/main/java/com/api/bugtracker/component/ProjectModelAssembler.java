package com.api.bugtracker.component;

import com.api.bugtracker.model.Project;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.stereotype.Component;

@Component
public class ProjectModelAssembler implements RepresentationModelAssembler<Project, EntityModel<Project>> {

    @Override
    public EntityModel<Project> toModel(Project project) {

        return EntityModel.of(project,
                linkTo(methodOn(ProjectController.class).one(project.getId())).withSelfRel(),
                linkTo(methodOn(ProjectController.class).all()).withRel("projects"));
    }
}
