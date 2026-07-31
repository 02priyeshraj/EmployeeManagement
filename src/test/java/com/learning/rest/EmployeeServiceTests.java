package com.learning.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {
    //for service layer testing we use Mock data / not use actual database
    @InjectMocks
    EmpService ser;

    @Mock
    EmpRepository repo;

    @Test
    void shouldReturnAllEmployeesFromRepository(){
        List<Employee> l = List.of(
                new Employee("John",627.83,"DA"),
                new Employee("JAA",72.82,"QA")
        );

        when(repo.getAllEmployees()).thenReturn(l);
        List<Employee> res = ser.getAllEmployees();
        assertEquals(2,res.size());
        verify(repo).getAllEmployees();
    }

    @Test
    void shouldReturnEmptyEmployeeListWhenRepositoryHasNoEmployees(){
        when(repo.getAllEmployees()).thenReturn(List.of());

        List<Employee> res = ser.getAllEmployees();

        assertTrue(res.isEmpty());
        verify(repo).getAllEmployees();
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4})
    void shouldReturnEmployeeByIdWhenEmployeeExists(int id){
        Employee emp = new Employee("Johny",738.83,"IT");
        when(repo.getEmployeeById(id)).thenReturn(emp);
        Employee res  =  ser.getEmployeeById(id);
        assertEquals("Johny", res.getName());
        verify(repo).getEmployeeById(id);
    }

    @Test
    void shouldPropagateEmployeeNotFoundExceptionWhenEmployeeDoesNotExist(){
        when(repo.getEmployeeById(99)).thenThrow(new EmployeeNotFoundException("Employee with ID 99 not found."));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> ser.getEmployeeById(99));

        assertEquals("Employee with ID 99 not found.", exception.getMessage());
        verify(repo).getEmployeeById(99);
    }

    @Test
    void shouldReturnInsertedRowCountWhenSavingEmployee(){
        Employee emp = new Employee("ABC",738.83,"SALES");
        when(repo.addEmployee(emp)).thenReturn(1);
        int res =  ser.saveEmployee(emp);
        assertEquals(1,res);
        verify(repo).addEmployee(emp);
    }

    @Test
    void shouldReturnZeroWhenRepositoryDoesNotInsertEmployee(){
        Employee emp = new Employee("ABC",738.83,"SALES");
        when(repo.addEmployee(emp)).thenReturn(0);

        int res = ser.saveEmployee(emp);

        assertEquals(0, res);
        verify(repo).addEmployee(emp);
    }

    @Test
    void shouldReturnUpdatedRowCountWhenUpdatingEmployee(){
        Employee emp = new Employee("Johny",738.83,"IT");
        when(repo.updateEmployee(7, emp)).thenReturn(1);

        int res = ser.updateEmployee(7, emp);

        assertEquals(1, res);
        verify(repo).updateEmployee(7, emp);
    }

    @Test
    void shouldReturnZeroWhenUpdatingEmployeeDoesNotMatchAnyRecord(){
        Employee emp = new Employee("Johny",738.83,"IT");
        when(repo.updateEmployee(700, emp)).thenReturn(0);

        int res = ser.updateEmployee(700, emp);

        assertEquals(0, res);
        verify(repo).updateEmployee(700, emp);
    }

    @Test
    void shouldReturnDeletedRowCountWhenDeletingEmployee(){
        when(repo.deleteEmployee(5)).thenReturn(1);

        int res = ser.deleteEmployee(5);

        assertEquals(1, res);
        verify(repo).deleteEmployee(5);
    }

    @Test
    void shouldReturnZeroWhenDeletingEmployeeDoesNotMatchAnyRecord(){
        when(repo.deleteEmployee(500)).thenReturn(0);

        int res = ser.deleteEmployee(500);

        assertEquals(0, res);
        verify(repo).deleteEmployee(500);
    }

}
