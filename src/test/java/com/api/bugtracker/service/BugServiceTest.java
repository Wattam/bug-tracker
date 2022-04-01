package com.api.bugtracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.api.bugtracker.model.Bug;
import com.api.bugtracker.model.Project;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.model.User;
import com.api.bugtracker.repository.BugRepository;
import com.api.bugtracker.repository.ProjectRepository;
import com.api.bugtracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
public class BugServiceTest {

    @Autowired
    private BugService bugService;

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User user = new User(1L, "name1", "username1", "email1@email1", "password1");
    private Project project = new Project(1L, "name1", "description1", Status.OPEN, user);

    @BeforeEach
    private void setUp() {

        userRepository.save(user);
    }

    @Test
    @DirtiesContext
    void shouldReturnAllBugs() {

        projectRepository.save(project);

        bugRepository.save(
                new Bug(1L, "summary1", "description1", Status.OPEN, project, user, "date11", "date12", "date13"));
        bugRepository.save(
                new Bug(2L, "summary2", "description2", Status.CLOSED, project, user, "date21", "date22", "date23"));

        List<Bug> bugs = bugService.all();

        assertEquals(2, bugs.size());

        assertEquals(1L, bugs.get(0).getId());
        assertEquals("summary1", bugs.get(0).getSummary());
        assertEquals("description1", bugs.get(0).getDescription());
        assertEquals(Status.OPEN, bugs.get(0).getStatus());
        assertEquals(project.getId(), bugs.get(0).getProject().getId());
        assertEquals(user.getId(), bugs.get(0).getCreator().getId());
        assertEquals("date11", bugs.get(0).getCreatedAt());
        assertEquals("date12", bugs.get(0).getUpdatedAt());
        assertEquals("date13", bugs.get(0).getClosedAt());

        assertEquals(2L, bugs.get(1).getId());
        assertEquals("summary2", bugs.get(1).getSummary());
        assertEquals("description2", bugs.get(1).getDescription());
        assertEquals(Status.CLOSED, bugs.get(1).getStatus());
        assertEquals(project.getId(), bugs.get(1).getProject().getId());
        assertEquals(user.getId(), bugs.get(1).getCreator().getId());
        assertEquals("date21", bugs.get(1).getCreatedAt());
        assertEquals("date22", bugs.get(1).getUpdatedAt());
        assertEquals("date23", bugs.get(1).getClosedAt());
    }

    @Test
    void shouldNotReturnAnyBug() {

        List<Bug> bugs = bugService.all();

        assertTrue(bugs.isEmpty());
        assertEquals(0, bugs.size());
    }

    @Test
    @DirtiesContext
    void shouldReturnOneBug() {

        projectRepository.save(project);

        Bug expected = bugRepository
                .save(new Bug(1L, "summary", "description", Status.OPEN, project, user, "date1", "date2", "date3"));

        Bug actual = bugService.one(1L).get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSummary(), actual.getSummary());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getProject().getId(), actual.getProject().getId());
        assertEquals(expected.getCreator().getId(), actual.getCreator().getId());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
        assertEquals(expected.getClosedAt(), actual.getClosedAt());
    }

    @Test
    void shouldNotReturnOneProject() {

        assertTrue(bugService.one(1L).isEmpty());
        assertFalse(bugService.one(1L).isPresent());
    }

    @Test
    @DirtiesContext
    void shouldCreateNewBug() {

        projectRepository.save(project);

        Bug expected = bugRepository
                .save(new Bug(1L, "summary", "description", Status.OPEN, project, user, "date1", "date2", "date3"));
        expected = bugService.newBug(expected);

        Bug actual = bugRepository.findById(expected.getId()).get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSummary(), actual.getSummary());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getProject().getId(), actual.getProject().getId());
        assertEquals(expected.getCreator().getId(), actual.getCreator().getId());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
        assertEquals(expected.getClosedAt(), actual.getClosedAt());
    }

    @Test
    @DirtiesContext
    void shouldReplaceBug() {

        projectRepository.save(project);

        Bug bug = bugRepository
                .save(new Bug(1L, "summary", "description", Status.OPEN, project, user, "date1", "date2", "date3"));
        bug = bugRepository.save(bug);

        Bug expected = new Bug(1L, "summaryUpdated", "descriptionUpdated", Status.CLOSED, project, user, "date1Updated",
                "date2Updated", "date3Updated");

        bugService.replaceBug(expected, bug.getId());
        Bug actual = bugRepository.findById(bug.getId()).get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSummary(), actual.getSummary());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getProject().getId(), actual.getProject().getId());
        assertEquals(expected.getCreator().getId(), actual.getCreator().getId());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
        assertEquals(expected.getClosedAt(), actual.getClosedAt());
    }

    @Test
    @DirtiesContext
    void shouldDeleteBug() {

        projectRepository.save(project);

        Bug bug = new Bug(1L, "summary", "description", Status.OPEN, project, user, "date1", "date2", "date3");
        bug = bugRepository.save(bug);

        bugService.deleteBug(bug.getId());

        assertTrue(bugRepository.findById(bug.getId()).isEmpty());
        assertFalse(bugRepository.findById(bug.getId()).isPresent());
    }
}