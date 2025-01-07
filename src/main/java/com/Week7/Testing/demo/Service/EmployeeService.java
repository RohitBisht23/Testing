package com.Week7.Testing.demo.Service;

import com.Week7.Testing.demo.Dto.EmployeeDto;
import com.Week7.Testing.demo.Exception.ResourceNotFoundException;

import java.util.List;

public interface EmployeeService {
    EmployeeDto getEmployeeById(Long id);

    EmployeeDto createNewEmployee(EmployeeDto employee);

    List<EmployeeDto> getAllEmployee();

    EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto);

    Boolean deleteEmployeeById(Long id);
}
