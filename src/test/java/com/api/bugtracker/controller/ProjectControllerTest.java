package com.api.bugtracker.controller;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.api.bugtracker.controller.exception.ProjectNotFoundException;
import com.api.bugtracker.model.Project;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.model.User;
import com.api.bugtracker.service.ProjectService;
import com.google.common.collect.ImmutableList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    private User user = new User(1L, "name", "username", "email@email", "password");

    @BeforeEach
    public void setUp() {
        Mockito.reset(projectService);
    }

    @Test
    void shouldReturnAllProjects() throws Exception {

        Project project1 = new Project(1L, "name1", "description1", Status.OPEN, user);
        Project project2 = new Project(2L, "name2", "description2", Status.CLOSED, user);
        List<Project> projects = ImmutableList
                .<Project>builder()
                .add(project1)
                .add(project2)
                .build();

        when(projectService.all()).thenReturn(projects);

        mockMvc.perform(get("/projects").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(projects.size()))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[0].status", is("OPEN")))
                .andExpect(jsonPath("$[0].owner.id", is(1)))
                .andExpect(jsonPath("$[0].links.[0].rel", is("self")))
                .andExpect(jsonPath("$[0].links.[0].href", is("http://localhost/projects/1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].description", is("description2")))
                .andExpect(jsonPath("$[1].status", is("CLOSED")))
                .andExpect(jsonPath("$[1].owner.id", is(1)))
                .andExpect(jsonPath("$[1].links.[0].rel", is("self")))
                .andExpect(jsonPath("$[1].links.[0].href", is("http://localhost/projects/2")));
    }

    @Test
    void shouldReturnOneProject() throws Exception {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);

        when(projectService.one(Mockito.anyLong())).thenReturn(Optional.of(project));

        mockMvc.perform(get("/projects/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.owner.id", is(1)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/projects/1")));
    }

    @Test
    void shouldNotReturnOneProject() throws Exception {

        mockMvc.perform(get("/projects/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof ProjectNotFoundException))
                .andExpect(result -> assertEquals("Could not find project 1",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldCreateNewProject() throws Exception {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);

        when(projectService.newProject(Mockito.any(Project.class))).thenReturn(project);

        JSONObject userJsonObject = new JSONObject().put("id", 1);
        JSONArray userJsonArray = new JSONArray().put(userJsonObject);
        String projectJson = new JSONObject()
                .put("name", "name")
                .put("description", "description")
                .put("status", Status.OPEN)
                .put("owner", userJsonArray.get(0))
                .toString();

        mockMvc.perform(
                post("/projects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.owner.id", is(1)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/projects/1")));
    }

    @Test
    void shouldReplaceProject() throws Exception {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);

        when(projectService.one(Mockito.anyLong())).thenReturn(Optional.of(project));
        when(projectService.replaceProject(Mockito.any(Project.class), Mockito.anyLong())).thenReturn(project);

        JSONObject userJsonObject = new JSONObject().put("id", 1);
        JSONArray userJsonArray = new JSONArray().put(userJsonObject);
        String projectJson = new JSONObject()
                .put("name", "name")
                .put("description", "description")
                .put("status", Status.OPEN)
                .put("owner", userJsonArray.get(0))
                .toString();

        mockMvc.perform(
                put("/projects/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.owner.id", is(1)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/projects/1")));
    }

    @Test
    void shouldNotReplaceProject() throws Exception {

        JSONObject userJsonObject = new JSONObject().put("id", 1);
        JSONArray userJsonArray = new JSONArray().put(userJsonObject);
        String projectJson = new JSONObject()
                .put("name", "name")
                .put("description", "description")
                .put("status", Status.OPEN)
                .put("owner", userJsonArray.get(0))
                .toString();

        mockMvc.perform(
                put("/projects/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof ProjectNotFoundException))
                .andExpect(result -> assertEquals("Could not find project 1",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void shouldDeleteProject() throws Exception {

        Project project = new Project();

        when(projectService.one(Mockito.anyLong())).thenReturn(Optional.of(project));

        mockMvc.perform(delete("/projects/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteProject() throws Exception {

        mockMvc.perform(delete("/projects/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof ProjectNotFoundException))
                .andExpect(result -> assertEquals("Could not find project 1",
                        result.getResolvedException().getMessage()));
    }
}
