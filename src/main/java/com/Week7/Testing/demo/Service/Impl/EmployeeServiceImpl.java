package com.Week7.Testing.demo.Service.Impl;

import com.Week7.Testing.demo.Dto.EmployeeDto;
import com.Week7.Testing.demo.Entity.Employee;
import com.Week7.Testing.demo.Exception.ResourceNotFoundException;
import com.Week7.Testing.demo.Repository.EmployeeRepository;
import com.Week7.Testing.demo.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public EmployeeDto getEmployeeById(Long id) {
        log.info("Getting the employee by id");
        Employee employee = employeeRepository
                .findById(id)
                .orElseThrow( () -> {
                    log.error("Caught an error with id {}", id);
                    return new ResourceNotFoundException("The employee not found with id :" + id);
                });
        log.info("Successfully able to find the employee by id {}", id);
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @Override
    public EmployeeDto createNewEmployee(EmployeeDto employee) {
        log.info("Creating new employee with email {}", employee.getEmail());
        List<Employee> ifExists =  employeeRepository.findByEmail(employee.getEmail());

        if(!ifExists.isEmpty()) {
            log.info("The employee exists with email Id {}", employee.getEmail());
            throw new RuntimeException("Employee already present with this email :"+employee.getEmail());
        }

        Employee newEmployee = modelMapper.map(employee, Employee.class);
        Employee savedEmployee = employeeRepository.save(newEmployee);
        log.info("Employee is created successfully with email {} and id {}", employee.getEmail(), employee.getId());
        return modelMapper.map(savedEmployee, EmployeeDto.class);
    }

    @Override
    public List<EmployeeDto> getAllEmployee() {
        log.info("Getting all the employee");
        List<Employee> employeeList = employeeRepository.findAll();
        log.info("Getting the employee dto list");
        return employeeList
                .stream()
                .map(employeeEntity -> modelMapper.map(employeeEntity, EmployeeDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        log.info("Updating employee with id {}", employeeDto.getId());
        //If Employee is not present with this id then simply create it
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->{
                    log.info("Employee with id {} not found", id);
                    return new ResourceNotFoundException("The employee not found here with id "+id);
                });

        if(!employee.getEmail().equals(employeeDto.getEmail())) {
            log.error("Attempted to update employee with id {}", id);
            throw  new RuntimeException("the email of the employee cannot be updated");
        }
        //employeeDto.setId(null);
        modelMapper.map(employeeDto,employee);
        employee.setId(id);

        log.info("Employee is updated successfully");
        Employee savedEmployeeEntity = employeeRepository.save(employee);
        return modelMapper.map(savedEmployeeEntity, EmployeeDto.class);
    }


    @Override
    public Boolean deleteEmployeeById(Long id) {
        log.info("deleting employee with id {}", id);
        boolean exists = employeeRepository.existsById(id);

        if(!exists) {
            log.error("Employee not fount with id {}",id);
            throw  new ResourceNotFoundException("Employee not found with id "+id);

        }
        employeeRepository.deleteById(id);
        log.info("Employee successfully deleted with id {}",id);
        return true;
    }
}
