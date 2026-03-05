package com.example.student;

import com.example.student.controller.StudentController;
import com.example.student.generated.model.StudentRequest;
import com.example.student.generated.model.StudentResponse;
import com.example.student.security.JwtAuthenticationFilter;
import com.example.student.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = StudentController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    void createStudentReturns201() throws Exception {
        StudentRequest req = new StudentRequest();
        req.setFirstName("Bob");
        req.setLastName("Jones");
        req.setEmail("bob@example.com");

        StudentResponse sr = new StudentResponse();
        sr.setId(1L);
        sr.setFirstName("Bob");
        sr.setLastName("Jones");
        sr.setEmail("bob@example.com");

        when(studentService.createStudent(any(StudentRequest.class))).thenReturn(sr);

        mockMvc.perform(post("/api/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }
}
