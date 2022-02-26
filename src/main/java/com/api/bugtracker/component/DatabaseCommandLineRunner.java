package com.api.bugtracker.component;

import com.api.bugtracker.model.Bug;
import com.api.bugtracker.model.Project;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.model.User;
import com.api.bugtracker.repository.BugRepository;
import com.api.bugtracker.repository.ProjectRepository;
import com.api.bugtracker.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCommandLineRunner implements CommandLineRunner {

    @Autowired
    BugRepository bugRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        User user1 = new User(1L, "Name1", "Username1", "email1@email1", "password1");
        User user2 = new User(2L, "Name2", "Username2", "email2@email2", "password2");
        User user3 = new User(3L, "Name3", "Username3", "email1@email3", "password3");

        Project project1 = new Project(1L, "Name1", "Description1", Status.OPEN, user1);
        Project project2 = new Project(2L, "Name2", "Description2", Status.OPEN, user2);
        Project project3 = new Project(3L, "Name3", "Description3", Status.OPEN, user3);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        bugRepository.save(new Bug(1L, "Summary1", "Description1", Status.OPEN, project1, user1, "Yesterday", "", ""));
        bugRepository.save(new Bug(2L, "Summary2", "Description2", Status.OPEN, project2, user2, "Yesterday", "", ""));
        bugRepository.save(new Bug(3L, "Summary3", "Description3", Status.OPEN, project3, user3, "Yesterday", "", ""));
    }

}
