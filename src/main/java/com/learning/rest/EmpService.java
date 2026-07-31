package com.learning.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpService {

    @Autowired
    private  EmpRepository empRepository;

    public List<Employee> getAllEmployees(){
        return empRepository.getAllEmployees();
    }


    public int saveEmployee(Employee employee) {
        return empRepository.addEmployee(employee);
    }

    public Employee getEmployeeById(int id) {
        return empRepository.getEmployeeById(id);
    }

    public int updateEmployee(int id, Employee employee) {
        return empRepository.updateEmployee(id, employee);
    }

    public int deleteEmployee(int id) {
        return empRepository.deleteEmployee(id);
    }


}
