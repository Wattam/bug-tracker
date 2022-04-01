package com.api.bugtracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.api.bugtracker.model.Project;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.model.User;
import com.api.bugtracker.repository.ProjectRepository;
import com.api.bugtracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User user = new User(1L, "name1", "username1", "email1@email1", "password1");

    @BeforeEach
    private void setUp() {

        userRepository.save(user);
    }

    @Test
    @DirtiesContext
    void shouldReturnAllProjects() {

        projectRepository.save(new Project(1L, "name1", "description1", Status.OPEN, user));
        projectRepository.save(new Project(2L, "name2", "description2", Status.CLOSED, user));

        List<Project> projects = projectService.all();

        assertEquals(2, projects.size());

        assertEquals(1L, projects.get(0).getId());
        assertEquals("name1", projects.get(0).getName());
        assertEquals("description1", projects.get(0).getDescription());
        assertEquals(Status.OPEN, projects.get(0).getStatus());
        assertEquals(user.getId(), projects.get(0).getOwner().getId());

        assertEquals(2L, projects.get(1).getId());
        assertEquals("name2", projects.get(1).getName());
        assertEquals("description2", projects.get(1).getDescription());
        assertEquals(Status.CLOSED, projects.get(1).getStatus());
        assertEquals(user.getId(), projects.get(1).getOwner().getId());
    }

    @Test
    void shouldNotReturnAnyProject() {

        List<Project> projects = projectService.all();

        assertTrue(projects.isEmpty());
        assertEquals(0, projects.size());
    }

    @Test
    @DirtiesContext
    void shouldReturnOneProject() {

        Project expected = projectRepository.save(new Project(1L, "name", "description", Status.OPEN, user));

        Project actual = projectService.one(1L).get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getOwner().getId(), actual.getOwner().getId());
    }

    @Test
    void shouldNotReturnOneProject() {

        assertTrue(projectService.one(1L).isEmpty());
        assertFalse(projectService.one(1L).isPresent());
    }

    @Test
    @DirtiesContext
    void shouldCreateNewProject() {

        Project expected = new Project(1L, "name", "description", Status.OPEN, user);
        expected = projectService.newProject(expected);

        Project actual = projectRepository.findById(expected.getId()).get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getOwner().getId(), actual.getOwner().getId());
    }

    @Test
    @DirtiesContext
    void shouldReplaceProject() {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);
        project = projectRepository.save(project);

        Project expected = new Project(1L, "nameUpdated", "descriptionUpdated", Status.CLOSED, user);

        projectService.replaceProject(expected, project.getId());
        Project actual = projectRepository.findById(project.getId()).get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getOwner().getId(), actual.getOwner().getId());
    }

    @Test
    @DirtiesContext
    void shouldDeleteProject() {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);
        project = projectRepository.save(project);

        projectService.deleteProject(project.getId());

        assertTrue(projectRepository.findById(project.getId()).isEmpty());
        assertFalse(projectRepository.findById(project.getId()).isPresent());
    }
}