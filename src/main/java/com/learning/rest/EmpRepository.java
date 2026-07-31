package com.learning.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmpRepository{
    @Autowired
    JdbcTemplate jdbcTemplate;
    public List<Employee> getAllEmployees(){
        String sql = "SELECT * FROM employees";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Employee.class));

    }

    public int addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (name, salary, department) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, employee.getName(), employee.getSalary(), employee.getDepartment());
    }

    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Employee.class), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EmployeeNotFoundException("Employee with ID " + id + " not found.");
        }
    }

    public int updateEmployee(int id, Employee employee) {
        String sql = "UPDATE employees SET name = ?, salary = ?, department = ? WHERE id = ?";
        return jdbcTemplate.update(sql, employee.getName(), employee.getSalary(), employee.getDepartment(), id);
    }

    public int deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
