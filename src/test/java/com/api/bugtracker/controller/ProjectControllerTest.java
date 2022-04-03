package com.api.bugtracker.controller;

import com.api.bugtracker.controller.exception.projectException.ProjectClosedException;
import com.api.bugtracker.controller.exception.projectException.ProjectNotFoundException;
import com.api.bugtracker.model.Project;
import com.api.bugtracker.model.Status;
import com.api.bugtracker.model.User;
import com.api.bugtracker.service.ProjectService;
import com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    private final User user = new User(1L, "name", "username", "email@email", "password");

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

        Page<Project> page = new PageImpl<>(projects, PageRequest.of(0, 15), projects.size());

        Mockito.when(projectService.all(0, 15)).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(page.getTotalElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].name", Matchers.is("name1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].description", Matchers.is("description1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].status", Matchers.is("OPEN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].owner.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].links.[0].rel", Matchers.is("self")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].links.[0].href", Matchers.is("http://localhost/projects/1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].id", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].name", Matchers.is("name2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].description", Matchers.is("description2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].status", Matchers.is("CLOSED")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].owner.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].links.[0].rel", Matchers.is("self")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1].links.[0].href", Matchers.is("http://localhost/projects/2")));
    }

    @Test
    void shouldReturnOneProject() throws Exception {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);

        Mockito.when(projectService.one(Mockito.anyLong())).thenReturn(Optional.of(project));

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("description")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("OPEN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.owner.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.is("http://localhost/projects/1")));
    }

    @Test
    void shouldNotReturnOneProject() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof ProjectNotFoundException)
                )
                .andExpect(result -> Assertions.assertEquals("Could not find project 1",
                        result.getResolvedException().getMessage())
                );
    }

    @Test
    void shouldCreateNewProject() throws Exception {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);

        Mockito.when(projectService.newProject(Mockito.any(Project.class))).thenReturn(project);

        JSONObject userJsonObject = new JSONObject().put("id", 1);
        JSONArray userJsonArray = new JSONArray().put(userJsonObject);
        String projectJson = new JSONObject()
                .put("name", "name")
                .put("description", "description")
                .put("status", Status.OPEN)
                .put("owner", userJsonArray.get(0))
                .toString();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/projects")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("description")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("OPEN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.owner.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.is("http://localhost/projects/1")));
    }

    @Test
    void shouldReplaceProject() throws Exception {

        Project project = new Project(1L, "name", "description", Status.OPEN, user);

        Mockito.when(projectService.one(Mockito.anyLong())).thenReturn(Optional.of(project));
        Mockito.when(projectService.replaceProject(Mockito.any(Project.class), Mockito.anyLong())).thenReturn(project);

        JSONObject userJsonObject = new JSONObject().put("id", 1);
        JSONArray userJsonArray = new JSONArray().put(userJsonObject);
        String projectJson = new JSONObject()
                .put("name", "name")
                .put("description", "description")
                .put("status", Status.OPEN)
                .put("owner", userJsonArray.get(0))
                .toString();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/projects/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("description")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("OPEN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.owner.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.is("http://localhost/projects/1")));
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

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/projects/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof ProjectNotFoundException)
                )
                .andExpect(result -> Assertions.assertEquals(
                        "Could not find project 1",
                        result.getResolvedException().getMessage())
                );
    }

    @Test
    void shouldDeleteProject() throws Exception {

        Project project = new Project();

        Mockito.when(projectService.one(Mockito.anyLong())).thenReturn(Optional.of(project));

        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void shouldNotDeleteProject() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof ProjectNotFoundException)
                )
                .andExpect(result -> Assertions.assertEquals(
                        "Could not find project 1",
                        result.getResolvedException().getMessage())
                );
    }

    @Test
    void shouldCloseProject() throws Exception {

        Project project = new Project();
        project.setStatus(Status.OPEN);

        Mockito.when(projectService.one(Mockito.anyLong())).thenReturn(Optional.of(project));

        mockMvc.perform(MockMvcRequestBuilders.put("/projects/1/close").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void shouldNotCloseProjectCauseNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/projects/1/close").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof ProjectNotFoundException)
                )
                .andExpect(result -> Assertions.assertEquals(
                        "Could not find project 1",
                        result.getResolvedException().getMessage())
                );
    }

    @Test
    void shouldNotCloseProjectCauseClosed() throws Exception {

        Project project = new Project();
        project.setStatus(Status.CLOSED);

        Mockito.when(projectService.one(Mockito.anyLong())).thenReturn(Optional.of(project));

        mockMvc.perform(MockMvcRequestBuilders.put("/projects/1/close").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof ProjectClosedException)
                )
                .andExpect(result -> Assertions.assertEquals(
                        "Project 1 is closed",
                        result.getResolvedException().getMessage())
                );
    }
}