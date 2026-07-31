package com.learning.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(EmpController.class)
public class EmployeeControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    EmpService ser;
    @Test
    void shouldReturnAllEmployee() throws Exception {
        List<Employee> l = List.of(
                new Employee("John",627.83,"DA"),
                new Employee("JAA",72.82,"QA")
        );
        when(ser.getAllEmployees()).thenReturn(l);
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("John"))
                .andExpect(jsonPath("$.data[1].name").value("JAA"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shouldGetEmployeeById(int id) throws Exception {
        Employee emp = new Employee("Johny", 738.83, "IT");
        when(ser.getEmployeeById(id)).thenReturn(emp);
        mockMvc.perform(get("/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Johny"))
                .andExpect(jsonPath("$.data.salary").value(738.83))
                .andExpect(jsonPath("$.data.department").value("IT"));
    }

    @Test
    void shouldReturnNotFoundWhenEmployeeByIdDoesNotExist() throws Exception {
        when(ser.getEmployeeById(999)).thenThrow(new EmployeeNotFoundException("Employee with ID 999 not found."));

        mockMvc.perform(get("/employees/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee with ID 999 not found."));
    }

    @Test
    void shouldAddEmployee() throws Exception {
        when(ser.saveEmployee(any(Employee.class))).thenReturn(1);
        mockMvc.perform(post("/employee")
                .contentType("application/json")
                .content("{\"name\":\"ABC\",\"salary\":738.83,\"department\":\"SALES\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Employee added successfully"));
    }

    @Test
    void shouldReturnErrorWhenAddEmployeeFails() throws Exception {
        when(ser.saveEmployee(any(Employee.class))).thenReturn(0);

        mockMvc.perform(post("/employee")
                        .contentType("application/json")
                        .content("{\"name\":\"ABC\",\"salary\":738.83,\"department\":\"SALES\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Failed to add employee"));
    }

    @Test
    void shouldUpdateEmployeeWhenServiceReturnsPositiveCount() throws Exception {
        when(ser.updateEmployee(any(Integer.class), any(Employee.class))).thenReturn(1);

        mockMvc.perform(put("/employees/{id}", 1001)
                        .contentType("application/json")
                        .content("{\"name\":\"Updated\",\"salary\":900.0,\"department\":\"IT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Employee updated successfully"));
    }

    @Test
    void shouldReturnErrorWhenUpdateEmployeeFails() throws Exception {
        when(ser.updateEmployee(any(Integer.class), any(Employee.class))).thenReturn(0);

        mockMvc.perform(put("/employees/{id}", 1001)
                        .contentType("application/json")
                        .content("{\"name\":\"Updated\",\"salary\":900.0,\"department\":\"IT\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Failed to update employee"));
    }

    @Test
    void shouldDeleteEmployeeWhenServiceReturnsPositiveCount() throws Exception {
        when(ser.deleteEmployee(1001)).thenReturn(1);

        mockMvc.perform(delete("/employees/{id}", 1001))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    @Test
    void shouldReturnErrorWhenDeleteEmployeeFails() throws Exception {
        when(ser.deleteEmployee(1001)).thenReturn(0);

        mockMvc.perform(delete("/employees/{id}", 1001))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Failed to delete employee"));
    }

}
