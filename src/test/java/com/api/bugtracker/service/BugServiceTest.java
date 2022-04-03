package com.api.bugtracker.service;

import com.api.bugtracker.model.Bug;
import com.api.bugtracker.model.Project;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.model.User;
import com.api.bugtracker.repository.BugRepository;
import com.api.bugtracker.repository.ProjectRepository;
import com.api.bugtracker.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

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

    private final User user = new User(1L, "name1", "username1", "email1@email1", "password1");
    private final Project project = new Project(1L, "name1", "description1", Status.OPEN, user);

    @BeforeEach
    private void setUp() {

        userRepository.save(user);
        projectRepository.save(project);
    }

    @Test
    @DirtiesContext
    void shouldReturnAllBugs() {

        bugRepository.save(
                new Bug(1L, "summary1", "description1", Status.OPEN, project, user, "date11", "date12", "date13"));
        bugRepository.save(
                new Bug(2L, "summary2", "description2", Status.CLOSED, project, user, "date21", "date22", "date23"));

        Page<Bug> bugs = bugService.all(0, 15);

        Assertions.assertTrue(bugs.hasContent());
        Assertions.assertEquals(2, bugs.getTotalElements());
        Assertions.assertEquals(2, bugs.getNumberOfElements());

        Assertions.assertEquals(1L, bugs.getContent().get(0).getId());
        Assertions.assertEquals("summary1", bugs.getContent().get(0).getSummary());
        Assertions.assertEquals("description1", bugs.getContent().get(0).getDescription());
        Assertions.assertEquals(Status.OPEN, bugs.getContent().get(0).getStatus());
        Assertions.assertEquals(project.getId(), bugs.getContent().get(0).getProject().getId());
        Assertions.assertEquals(user.getId(), bugs.getContent().get(0).getCreator().getId());
        Assertions.assertEquals("date11", bugs.getContent().get(0).getCreatedAt());
        Assertions.assertEquals("date12", bugs.getContent().get(0).getUpdatedAt());
        Assertions.assertEquals("date13", bugs.getContent().get(0).getClosedAt());

        Assertions.assertEquals(2L, bugs.getContent().get(1).getId());
        Assertions.assertEquals("summary2", bugs.getContent().get(1).getSummary());
        Assertions.assertEquals("description2", bugs.getContent().get(1).getDescription());
        Assertions.assertEquals(Status.CLOSED, bugs.getContent().get(1).getStatus());
        Assertions.assertEquals(project.getId(), bugs.getContent().get(1).getProject().getId());
        Assertions.assertEquals(user.getId(), bugs.getContent().get(1).getCreator().getId());
        Assertions.assertEquals("date21", bugs.getContent().get(1).getCreatedAt());
        Assertions.assertEquals("date22", bugs.getContent().get(1).getUpdatedAt());
        Assertions.assertEquals("date23", bugs.getContent().get(1).getClosedAt());
    }

    @Test
    void shouldNotReturnAnyBug() {

        Page<Bug> bugs = bugService.all(0, 15);

        Assertions.assertTrue(bugs.isEmpty());
        Assertions.assertEquals(0, bugs.getTotalElements());
        Assertions.assertEquals(0, bugs.getNumberOfElements());
    }

    @Test
    @DirtiesContext
    void shouldReturnOneBug() {

        Bug expected = bugRepository
                .save(new Bug(1L, "summary", "description", Status.OPEN, project, user, "date1", "date2", "date3"));

        Bug actual = bugService.one(1L).get();

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getSummary(), actual.getSummary());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
        Assertions.assertEquals(expected.getProject().getId(), actual.getProject().getId());
        Assertions.assertEquals(expected.getCreator().getId(), actual.getCreator().getId());
        Assertions.assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        Assertions.assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
        Assertions.assertEquals(expected.getClosedAt(), actual.getClosedAt());
    }

    @Test
    void shouldNotReturnOneProject() {

        Assertions.assertTrue(bugService.one(1L).isEmpty());
        Assertions.assertFalse(bugService.one(1L).isPresent());
    }

    @Test
    @DirtiesContext
    void shouldCreateNewBug() {

        Bug expected = bugRepository
                .save(new Bug(1L, "summary", "description", Status.OPEN, project, user, "date1", "date2", "date3"));
        expected = bugService.newBug(expected);

        Bug actual = bugRepository.findById(expected.getId()).get();

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getSummary(), actual.getSummary());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
        Assertions.assertEquals(expected.getProject().getId(), actual.getProject().getId());
        Assertions.assertEquals(expected.getCreator().getId(), actual.getCreator().getId());
        Assertions.assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        Assertions.assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
        Assertions.assertEquals(expected.getClosedAt(), actual.getClosedAt());
    }

    @Test
    @DirtiesContext
    void shouldReplaceBug() {

        Bug bug = bugRepository
                .save(new Bug(1L, "summary", "description", Status.OPEN, project, user, "date1", "date2", "date3"));
        bug = bugRepository.save(bug);

        Bug expected = new Bug(1L, "summaryUpdated", "descriptionUpdated", Status.CLOSED, project, user, "date1Updated",
                "date2Updated", "date3Updated");

        bugService.replaceBug(expected, bug.getId());
        Bug actual = bugRepository.findById(bug.getId()).get();

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getSummary(), actual.getSummary());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
        Assertions.assertEquals(expected.getProject().getId(), actual.getProject().getId());
        Assertions.assertEquals(expected.getCreator().getId(), actual.getCreator().getId());
        Assertions.assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        Assertions.assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
        Assertions.assertEquals(expected.getClosedAt(), actual.getClosedAt());
    }

    @Test
    @DirtiesContext
    void shouldDeleteBug() {

        Bug bug = new Bug(1L, "summary", "description", Status.OPEN, project, user, "date1", "date2", "date3");
        bug = bugRepository.save(bug);

        bugService.deleteBug(bug.getId());

        Assertions.assertTrue(bugRepository.findById(bug.getId()).isEmpty());
        Assertions.assertFalse(bugRepository.findById(bug.getId()).isPresent());
    }

    @Test
    @DirtiesContext
    @Transactional
    void shouldCloseBug() {

        Bug bug = new Bug(1L, "summary", "description", Status.OPEN, project, user, "date1", "date2", "date3");
        bug = bugRepository.save(bug);

        bugService.closeBug(bug.getId());

        Assertions.assertEquals(
                Status.CLOSED,
                bugRepository.findById(bug.getId()).get().getStatus()
        );
    }
}