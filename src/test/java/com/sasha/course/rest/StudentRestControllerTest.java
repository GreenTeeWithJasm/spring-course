package com.sasha.course.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasha.course.dao.StudentDAO;
import com.sasha.course.entities.Student.Student;
import com.sasha.course.entities.Student.StudentResponseMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentRestControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private StudentRestController studentRestController;

    @MockBean
    private StudentDAO studentDAO;

    @Autowired
    private MockMvc mockMvc;

    private Student getTestStudent() {
        return new Student("test", "student", "test@user.com");
    }

    private Student getTestStudent(Integer id) {
        Student student = getTestStudent();
        student.setId(id);

        return student;
    }

    @Test
    public void createStudentWithValidParams() throws Exception {
        doAnswer(invocationOnMock -> {
            Student newStudent = invocationOnMock.getArgument(0);
            newStudent.setId(1);
            return null;
        }).when(studentDAO).save(any(Student.class));

        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders.post("http://localhost:%s/students".formatted(port))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getTestStudent())))
                        .andReturn();

        JsonNode jsonNodeFromResponse = new ObjectMapper().readTree(response.getResponse().getContentAsString());

        assertEquals(HttpStatus.CREATED.value(), response.getResponse().getStatus());
        assertEquals(HttpStatus.CREATED.value(), jsonNodeFromResponse.get("status").asInt());
    }

    @Test
    public void createExistingStudentWithValidParams() throws Exception {
        doThrow(DataIntegrityViolationException.class).when(studentDAO).save(any(Student.class));

        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders.post("http://localhost:%s/students".formatted(port))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(getTestStudent())))
                        .andReturn();

        JsonNode jsonNodeFromResponse = new ObjectMapper().readTree(response.getResponse().getContentAsString());

        assertEquals(HttpStatus.CONFLICT.value(), response.getResponse().getStatus());
        assertEquals(HttpStatus.CONFLICT.value(), jsonNodeFromResponse.get("status").asInt());
    }

    @Test
    public void getStudentById() throws Exception {
        Student testStudent = getTestStudent(1);
        StudentResponseWithData<Student> expectedResponse = new StudentResponseWithData<>(
                StudentResponseMessage.SUCCESS,
                HttpStatus.OK,
                testStudent
        );

        when(studentDAO.findById(1)).thenReturn(testStudent);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:%s/students/1".formatted(port)))
                .andReturn();

        JsonNode jsonNodeFromResponse = new ObjectMapper().readTree(response.getResponse().getContentAsString());

        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertEquals(expectedResponse.getStatus(), jsonNodeFromResponse.get("status").asInt());
        assertEquals(expectedResponse.getMessage(), jsonNodeFromResponse.get("message").asText());
        assertEquals(expectedResponse.getData().getId(), jsonNodeFromResponse.get("data").get("id").asInt());
        assertEquals(expectedResponse.getData().getLastName(), jsonNodeFromResponse.get("data").get("lastName").asText());
    }

    @Test
    public void getNotExistingStudentById() throws Exception {
        when(studentDAO.findById(1)).thenReturn(null);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:%s/students/1".formatted(port)))
                .andReturn();

        JsonNode response = new ObjectMapper().readTree(result.getResponse().getContentAsString());

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.get("status").asInt());
    }

    @Test
    public void getStudentsByLastName() throws Exception {
        String TEST_USER_NAME = "test last name";

        List<Student> mockList = List.of(
                getTestStudent(),
                getTestStudent()
        );

        when(studentDAO.findByLastName(TEST_USER_NAME)).thenReturn(mockList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:%s/students".formatted(port)).param("lastName", TEST_USER_NAME))
                .andReturn();

        JsonNode json = new ObjectMapper().readTree(result.getResponse().getContentAsString());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(HttpStatus.OK.value(), json.get("status").asInt());
        assertEquals(new ObjectMapper().writeValueAsString(mockList), json.get("data").toString());
    }

    @Test
    public void getStudentsByNonExistingLastName() throws Exception {
        String TEST_USER_NAME = "test last name";

        List<Student> mockList = List.of();

        when(studentDAO.findByLastName(TEST_USER_NAME)).thenReturn(mockList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:%s/students".formatted(port)).param("lastName", TEST_USER_NAME))
                .andReturn();

        JsonNode json = new ObjectMapper().readTree(result.getResponse().getContentAsString());

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertEquals(HttpStatus.NOT_FOUND.value(), json.get("status").asInt());
    }

    @Test
    public void getAllStudents() throws Exception {
        List<Student> mockList = List.of(
                getTestStudent(),
                getTestStudent()
        );

        when(studentDAO.findAll()).thenReturn(mockList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:%s/students".formatted(port)))
                .andReturn();

        JsonNode json = new ObjectMapper().readTree(result.getResponse().getContentAsString());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(HttpStatus.OK.value(), json.get("status").asInt());
        assertEquals(new ObjectMapper().writeValueAsString(mockList), json.get("data").toString());
    }

    @Test
    public void getAllStudentsWithNoStudentsInDB() throws Exception {
        List<Student> mockList = List.of();

        when(studentDAO.findAll()).thenReturn(mockList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:%s/students".formatted(port)))
                .andReturn();

        JsonNode json = new ObjectMapper().readTree(result.getResponse().getContentAsString());

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertEquals(HttpStatus.NOT_FOUND.value(), json.get("status").asInt());
    }

    @Test
    public void updateSingleStudent() throws Exception {
        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders.put("http://localhost:%s/students".formatted(port))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(getTestStudent())))
                .andReturn();

        JsonNode jsonNodeFromResponse = new ObjectMapper().readTree(response.getResponse().getContentAsString());

        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertEquals(HttpStatus.OK.value(), jsonNodeFromResponse.get("status").asInt());
    }

    @Test
    public void updateStudentsLastNames() throws Exception {
        when(studentDAO.updateAllLastNames(anyString(), anyString())).thenReturn(4);

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders.put("http://localhost:%s/students".formatted(port))
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("oldLastName", "user")
                                .param("newLastName", "student"))
                .andReturn();

        JsonNode jsonNodeFromResponse = new ObjectMapper().readTree(response.getResponse().getContentAsString());

        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertEquals(HttpStatus.OK.value(), jsonNodeFromResponse.get("status").asInt());
        assertEquals(4, jsonNodeFromResponse.get("data").asInt());
    }

    @Test
    public void updateStudentsLastNamesWithNoMatches() throws Exception {
        when(studentDAO.updateAllLastNames(anyString(), anyString())).thenReturn(0);

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders.put("http://localhost:%s/students".formatted(port))
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("oldLastName", "user")
                                .param("newLastName", "student"))
                .andReturn();

        JsonNode jsonNodeFromResponse = new ObjectMapper().readTree(response.getResponse().getContentAsString());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
        assertEquals(HttpStatus.NOT_FOUND.value(), jsonNodeFromResponse.get("status").asInt());
    }

    @Test
    public void notValidUpdateRequest() throws Exception {
        when(studentDAO.updateAllLastNames(anyString(), anyString())).thenReturn(0);

        MvcResult response = mockMvc.perform(
                        MockMvcRequestBuilders.put("http://localhost:%s/students".formatted(port))
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("oldLastName", "user")
                                .content(new ObjectMapper().writeValueAsString(getTestStudent())))
                .andReturn();

        JsonNode jsonNodeFromResponse = new ObjectMapper().readTree(response.getResponse().getContentAsString());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.value(), jsonNodeFromResponse.get("status").asInt());
    }

    @Test
    public void deleteStudent() throws Exception {
        ProxyStudent proxyStudent = new ProxyStudent(getTestStudent(1));
        when(studentDAO.findById(1)).thenReturn(proxyStudent.getProxyStudent());
        doAnswer(invocationOnMock -> {
            proxyStudent.removeStudent();
            return null;
        }).when(studentDAO).delete(any(Student.class));

        MvcResult response = mockMvc.perform(
            MockMvcRequestBuilders.delete("http://localhost:%s/students/1".formatted(port))
        ).andReturn();

        JsonNode jsonNodeFromResponse = new ObjectMapper().readTree(response.getResponse().getContentAsString());

        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
        assertEquals(HttpStatus.OK.value(), jsonNodeFromResponse.get("status").asInt());
        assertNull(proxyStudent.getProxyStudent());
    }

    @Test
    public void deleteNotExistingStudent() throws Exception {
        when(studentDAO.findById(1)).thenReturn(null);

        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders.delete("http://localhost:%s/students/1".formatted(port))
        ).andReturn();

        JsonNode jsonNodeFromResponse = new ObjectMapper().readTree(response.getResponse().getContentAsString());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
        assertEquals(HttpStatus.NOT_FOUND.value(), jsonNodeFromResponse.get("status").asInt());
    }
}

class ProxyStudent {
    private Student proxyStudent;

    public ProxyStudent(Student student) {
        this.proxyStudent = student;
    }

    public Student getProxyStudent() {
        return proxyStudent;
    }

    public void removeStudent() {
        proxyStudent = null;
    }
}
