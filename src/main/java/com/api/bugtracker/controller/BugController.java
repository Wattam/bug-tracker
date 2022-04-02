package com.api.bugtracker.controller;

import com.api.bugtracker.component.BugModelAssembler;
import com.api.bugtracker.controller.exception.bugException.BugClosedException;
import com.api.bugtracker.controller.exception.bugException.BugNotFoundException;
import com.api.bugtracker.model.Bug;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/bugs")
public class BugController {

    @Autowired
    private BugService bugService;

    @Autowired
    private BugModelAssembler bugAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EntityModel<Bug>> all(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size
    ) {

        return bugService.all(page, size).map(bugAssembler::toModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Bug>> one(@PathVariable long id) {

        return ResponseEntity.ok(
                bugAssembler.toModel(
                        bugService.one(id).orElseThrow(() -> new BugNotFoundException(id))
                )
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<Bug>> newBug(@Valid @RequestBody Bug bug) {

        EntityModel<Bug> bugEM = bugAssembler.toModel(
                bugService.newBug(bug));

        return ResponseEntity
                .created(bugEM.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(bugEM);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Bug>> replaceBug(@Valid @RequestBody Bug bug, @PathVariable long id) {

        bugService.one(id).orElseThrow(() -> new BugNotFoundException(id));

        EntityModel<Bug> bugEM = bugAssembler.toModel(
                bugService.replaceBug(bug, id));

        return ResponseEntity
                .created(bugEM.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(bugEM);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBug(@PathVariable long id) {

        bugService.one(id).orElseThrow(() -> new BugNotFoundException(id));

        bugService.deleteBug(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<?> closeBug(@PathVariable long id) {

        if (bugService.one(id).orElseThrow(() -> new BugNotFoundException(id)).getStatus()
                .equals(Status.CLOSED)) {

            throw new BugClosedException(id);
        }

        bugService.closeBug(id);
        return ResponseEntity.noContent().build();
    }
}