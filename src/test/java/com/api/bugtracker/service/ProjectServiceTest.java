package com.api.bugtracker.service;

import com.api.bugtracker.model.Project;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.model.User;
import com.api.bugtracker.repository.ProjectRepository;
import com.api.bugtracker.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private final User user = new User(1L, "name1", "username1", "email1@email1", "password1");

    @BeforeEach
    private void setUp() {

        userRepository.save(user);
    }

    @Test
    @DirtiesContext
    void shouldReturnAllProjects() {

        projectRepository.save(new Project(1L, "name1", "description1", Status.OPEN, user));
        projectRepository.save(new Project(2L, "name2", "description2", Status.CLOSED, user));

        Page<Project> projects = projectService.all(0, 15);

        Assertions.assertTrue(projects.hasContent());
        Assertions.assertEquals(2, projects.getTotalElements());
        Assertions.assertEquals(2, projects.getNumberOfElements());

        Assertions.assertEquals(1L, projects.getContent().get(0).getId());
        Assertions.assertEquals("name1", projects.getContent().get(0).getName());
        Assertions.assertEquals("description1", projects.getContent().get(0).getDescription());
        Assertions.assertEquals(Status.OPEN, projects.getContent().get(0).getStatus());
        Assertions.assertEquals(user.getId(), projects.getContent().get(0).getOwner().getId());

        Assertions.assertEquals(2L, projects.getContent().get(1).getId());
        Assertions.assertEquals("name2", projects.getContent().get(1).getName());
        Assertions.assertEquals("description2", projects.getContent().get(1).getDescription());
        Assertions.assertEquals(Status.CLOSED, projects.getContent().get(1).getStatus());
        Assertions.assertEquals(user.getId(), projects.getContent().get(1).getOwner().getId());
    }

    @Test
    void shouldNotReturnAnyProject() {

        Page<Project> projects = projectService.all(0, 15);

        Assertions.assertTrue(projects.isEmpty());
        Assertions.assertEquals(0, projects.getTotalElements());
        Assertions.assertEquals(0, projects.getNumberOfElements());
    }

    @Test
    @DirtiesContext
    void shouldReturnOneProject() {

        Project expected = projectRepository.save(new Project(1L, "name", "description", Status.OPEN, user));

        Project actual = projectService.one(1L).get();

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
        Assertions.assertEquals(expected.getOwner().getId(), actual.getOwner().getId());
    }

    @Test
    void shouldNotReturnOneProject() {

        Assertions.assertTrue(projectService.one(1L).isEmpty());
        Assertions.assertFalse(projectService.one(1L).isPresent());
    }

    @Test
    @DirtiesContext
    void shouldCreateNewProject() {

        Project expected = new Project(1L, "name", "description", Status.OPEN, user);
        expected = projectService.newProject(expected);

        Project actual = projectRepository.findById(expected.getId()).get();

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
        Assertions.assertEquals(expected.getOwner().getId(), actual.getOwner().getId());
    }

    @Test
    @DirtiesContext
    void shouldReplaceProject() {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);
        project = projectRepository.save(project);

        Project expected = new Project(1L, "nameUpdated", "descriptionUpdated", Status.CLOSED, user);

        projectService.replaceProject(expected, project.getId());
        Project actual = projectRepository.findById(project.getId()).get();

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
        Assertions.assertEquals(expected.getOwner().getId(), actual.getOwner().getId());
    }

    @Test
    @DirtiesContext
    void shouldDeleteProject() {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);
        project = projectRepository.save(project);

        projectService.deleteProject(project.getId());

        Assertions.assertTrue(projectRepository.findById(project.getId()).isEmpty());
        Assertions.assertFalse(projectRepository.findById(project.getId()).isPresent());
    }
}