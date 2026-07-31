package com.learning.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Import(EmpRepository.class)
public class EmployeeRepoTests {
    @Autowired
    EmpRepository repo;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearEmployeesTable() {
        jdbcTemplate.update("DELETE FROM employees");
    }

    @Test
    void shouldReturnAllEmployeesWhenTableHasRecords(){
        jdbcTemplate.update("INSERT INTO employees (id, name, salary, department) VALUES (?, ?, ?, ?)", 1001, "John", 627.83, "DA");
        jdbcTemplate.update("INSERT INTO employees (id, name, salary, department) VALUES (?, ?, ?, ?)", 1002, "Jane", 728.10, "QA");

        List<Employee> employees = repo.getAllEmployees();

        assertEquals(2, employees.size());
        assertTrue(employees.stream().anyMatch(employee -> employee.getId() == 1001 && "John".equals(employee.getName()) && "DA".equals(employee.getDepartment())));
        assertTrue(employees.stream().anyMatch(employee -> employee.getId() == 1002 && "Jane".equals(employee.getName()) && "QA".equals(employee.getDepartment())));
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist(){
        List<Employee> employees = repo.getAllEmployees();

        assertTrue(employees.isEmpty());
    }

    @Test
    void shouldReturnEmployeeByIdWhenEmployeeExists(){
        jdbcTemplate.update("INSERT INTO employees (id, name, salary, department) VALUES (?, ?, ?, ?)", 1001, "User1001", 500.0, "IT");

        Employee employee = repo.getEmployeeById(1001);

        assertNotNull(employee);
        assertEquals(1001, employee.getId());
        assertEquals("User1001", employee.getName());
        assertEquals(500.0, employee.getSalary());
        assertEquals("IT", employee.getDepartment());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeIdDoesNotExist(){
        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> repo.getEmployeeById(9999));

        assertEquals("Employee with ID 9999 not found.", exception.getMessage());
    }

    @Test
    void shouldAddEmployeeAndPersistIt(){
        Employee employee = new Employee("ABC",738.83,"SALES");

        int rowsInserted = repo.addEmployee(employee);
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employees WHERE name = ? AND salary = ? AND department = ?", Integer.class, "ABC", 738.83, "SALES");

        assertEquals(1, rowsInserted);
        assertEquals(1, count);
    }

    @Test
    void shouldRejectEmployeeWhenRequiredNameIsMissing(){
        Employee employee = new Employee(null,738.83,"SALES");

        assertThrows(DataIntegrityViolationException.class, () -> repo.addEmployee(employee));
    }

    @Test
    void shouldUpdateExistingEmployeeDetails(){
        jdbcTemplate.update("INSERT INTO employees (id, name, salary, department) VALUES (?, ?, ?, ?)", 1001, "John", 627.83, "DA");
        Employee updatedEmployee = new Employee("Johnny", 700.00, "IT");

        int rowsUpdated = repo.updateEmployee(1001, updatedEmployee);
        Employee persistedEmployee = repo.getEmployeeById(1001);

        assertEquals(1, rowsUpdated);
        assertEquals("Johnny", persistedEmployee.getName());
        assertEquals(700.00, persistedEmployee.getSalary());
        assertEquals("IT", persistedEmployee.getDepartment());
    }

    @Test
    void shouldReturnZeroWhenUpdatingMissingEmployee(){
        Employee updatedEmployee = new Employee("Johnny", 700.00, "IT");

        int rowsUpdated = repo.updateEmployee(4040, updatedEmployee);

        assertEquals(0, rowsUpdated);
    }

    @Test
    void shouldDeleteExistingEmployee(){
        jdbcTemplate.update("INSERT INTO employees (id, name, salary, department) VALUES (?, ?, ?, ?)", 1001, "John", 627.83, "DA");

        int rowsDeleted = repo.deleteEmployee(1001);
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employees WHERE id = ?", Integer.class, 1001);

        assertEquals(1, rowsDeleted);
        assertEquals(0, count);
    }

    @Test
    void shouldReturnZeroWhenDeletingMissingEmployee(){
        int rowsDeleted = repo.deleteEmployee(4040);

        assertEquals(0, rowsDeleted);
    }

}
