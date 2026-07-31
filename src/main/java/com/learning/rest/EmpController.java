package com.learning.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EmpController {
    @Autowired
    EmpService empService;

    @GetMapping ("/employees")
    public ResponseEntity<Map<String,Object>> getAllEmployees(){
        Map<String,Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("data", empService.getAllEmployees());
        return ResponseEntity.status(200).body(res);
    }


    @PostMapping("/employee")
    public ResponseEntity<Map<String,Object>> addEmployee(@RequestBody Employee employee){
        int res = empService.saveEmployee(employee);
        if (res > 0) {
            Map<String,Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Employee added successfully");
            return ResponseEntity.status(201).body(response);
        } else {
            Map<String,Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to add employee");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Map<String,Object>> getEmployeeById(@PathVariable int id) {
        Employee employee = empService.getEmployeeById(id);
        Map<String,Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", employee);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Map<String,Object>> updateEmployee(@PathVariable int id, @RequestBody Employee employee) {
        int updatedRows = empService.updateEmployee(id, employee);
        if (updatedRows > 0) {
            Map<String,Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Employee updated successfully");
            return ResponseEntity.status(200).body(response);
        } else {
            Map<String,Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to update employee");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String,Object>> deleteEmployee(@PathVariable int id) {
        int deletedRows = empService.deleteEmployee(id);
        if (deletedRows > 0) {
            Map<String,Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Employee deleted successfully");
            return ResponseEntity.status(200).body(response);
        } else {
            Map<String,Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to delete employee");
            return ResponseEntity.status(500).body(response);
        }
    }

}
