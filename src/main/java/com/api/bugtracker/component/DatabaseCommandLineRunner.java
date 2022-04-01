//package com.api.bugtracker.component;
//
//import com.api.bugtracker.model.Bug;
//import com.api.bugtracker.model.Project;
//import com.api.bugtracker.model.Status;
//import com.api.bugtracker.model.User;
//import com.api.bugtracker.repository.BugRepository;
//import com.api.bugtracker.repository.ProjectRepository;
//import com.api.bugtracker.repository.UserRepository;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DatabaseCommandLineRunner implements CommandLineRunner {
//
//    @Autowired
//    BugRepository bugRepository;
//    @Autowired
//    ProjectRepository projectRepository;
//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    public void run(String... args) {
//
//        User user1 = new User(
//                1L,
//                "Nalbert Wattam",
//                "nalbertwattam",
//                "nalbertwattam@gmail.com",
//                "password1"
//        );
//        User user2 = new User(
//                2L,
//                "Rust Cohle",
//                "rustcohle",
//                "rustcohle@gmail.com",
//                "password2"
//        );
//        User user3 = new User(
//                3L,
//                "Martin Hart",
//                "martinhart",
//                "martinhart@gmail.com",
//                "password3"
//        );
//
//        Project project1 = new Project(
//                1L,
//                "Book Store API",
//                "A REST API for a book store",
//                Status.OPEN,
//                user1
//        );
//        Project project2 = new Project(
//                2L,
//                "Serial Killers API",
//                "A REST API to manage serial killers on Erath, Louisiana",
//                Status.OPEN,
//                user2
//        );
//        Project project3 = new Project(
//                3L,
//                "Coffee Shops API",
//                "A REST API to manage coffee shops on Erath, Louisiana",
//                Status.OPEN,
//                user3
//        );
//
//        userRepository.save(user1);
//        userRepository.save(user2);
//        userRepository.save(user3);
//
//        projectRepository.save(project1);
//        projectRepository.save(project2);
//        projectRepository.save(project3);
//
//        bugRepository.save(
//                new Bug(
//                        1L,
//                        "Error on updating books",
//                        "When trying to update a book the error \"Method Not Allowed\" (code 405) is throw",
//                        Status.OPEN,
//                        project1,
//                        user1,
//                        "27/02/2022 00:04",
//                        null,
//                        null)
//        );
//        bugRepository.save(
//                new Bug(
//                        2L,
//                        "Error on showing murders by date",
//                        "When trying to show murders by date the API don't show any murder",
//                        Status.OPEN,
//                        project2,
//                        user2,
//                        "22/05/1995 13:50",
//                        "25/05/2012 21:30",
//                        null)
//        );
//        bugRepository.save(
//                new Bug(
//                        3L,
//                        "Unable to delete a coffee shop",
//                        "When trying to delete a coffee shop it says that it was deleted, but it wasn't",
//                        Status.CLOSED,
//                        project3,
//                        user3,
//                        "11/01/2012 09:50",
//                        "13/01/2012 11:50",
//                        "14/01/2012 08:30")
//        );
//    }
//}