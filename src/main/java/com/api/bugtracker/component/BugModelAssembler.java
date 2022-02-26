package com.api.bugtracker.component;

import com.api.bugtracker.model.Bug;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.stereotype.Component;

@Component
public class BugModelAssembler implements RepresentationModelAssembler<Bug, EntityModel<Bug>> {

    @Override
    public EntityModel<Bug> toModel(Bug bug) {

        return EntityModel.of(bug,
                linkTo(methodOn(BugController.class).one(bug.getId())).withSelfRel(),
                linkTo(methodOn(BugController.class).all()).withRel("bugs"));
    }
}
