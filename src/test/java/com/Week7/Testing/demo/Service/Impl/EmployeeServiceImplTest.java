package com.Week7.Testing.demo.Service.Impl;


import com.Week7.Testing.demo.Dto.EmployeeDto;
import com.Week7.Testing.demo.Entity.Employee;
import com.Week7.Testing.demo.Exception.ResourceNotFoundException;
import com.Week7.Testing.demo.Repository.EmployeeRepository;
import com.Week7.Testing.demo.TestContainerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee mockedEmployee;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        mockedEmployee = Employee.builder()
                .id(1L)
                .name("Rohit Bisht")
                .email("Rohitbisht0911@gmail.com")
                .salary(3000L)
                .build();

        employeeDto = modelMapper.map(mockedEmployee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_whenIdIsPresent_ThenReturnEmployeeDto() {

        //Assign
        Long id = mockedEmployee.getId();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockedEmployee)); //stubbing

        //Act
        EmployeeDto employeeDto = employeeService.getEmployeeById(id);

        //Asset
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(id);
        assertThat((employeeDto.getEmail())).isEqualTo(mockedEmployee.getEmail());
        verify(employeeRepository).findById(id);
    }

    @Test
    void testGetEmployeeById_whenIdIsNotPresent_thenThrowException() {
        //arrange
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act And Assert combined
        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("The employee not found with id :1");

        verify(employeeRepository).findById(1L);
    }

    @Test
    void testCreateNewEmployee_whenValidEmployeeDto() {
        //Arrange
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockedEmployee);

        //Act
        EmployeeDto employeeDTO = employeeService.createNewEmployee(employeeDto);

        //Assert
        assertThat(employeeDTO).isNotNull();
        assertThat(employeeDTO.getId()).isEqualTo(mockedEmployee.getId());
        assertThat(employeeDTO.getEmail()).isEqualTo(mockedEmployee.getEmail());
        verify(employeeRepository, atLeastOnce()).findByEmail(any());

        //Create an argumentCaptor for example
        ArgumentCaptor<Employee> emplyeeCaptor = ArgumentCaptor.forClass(Employee.class);


        verify(employeeRepository, atLeast(1)).save(emplyeeCaptor.capture());
        Employee capturedEmployee = emplyeeCaptor.getValue();

        assertThat(capturedEmployee.getEmail()).isEqualTo(mockedEmployee.getEmail());
        assertThat(capturedEmployee.getId()).isEqualTo(mockedEmployee.getId());
    }

    @Test
    void testCreateNewEmployee_whenAttemptingCreateNewEmployeeWithExistingEmployee_thenthrowException() {
        //Arrange
        when(employeeRepository.findByEmail(mockedEmployee.getEmail())).thenReturn(List.of(mockedEmployee));

        //act AND assert
        assertThatThrownBy(() -> employeeService.createNewEmployee(employeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already present with this email :"+mockedEmployee.getEmail());
        verify(employeeRepository, atLeastOnce()).findByEmail(mockedEmployee.getEmail());
        verify(employeeRepository, never()).save(any());
    }


    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenthrowException() {
        //arrange
        when(employeeRepository.findById(employeeDto.getId())).thenReturn(Optional.of(mockedEmployee));
        employeeDto.setName("Rohit Bisht");
        employeeDto.setEmail("rohitbisht0911@gmail.com");

        //act + assert
        assertThatThrownBy(() -> employeeService.updateEmployee(mockedEmployee.getId(), employeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("the email of the employee cannot be updated");

        verify(employeeRepository, atLeastOnce()).findById(mockedEmployee.getId());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_WhenValidEmployee_thenUpdateEmployee() {
        //arrange
        when(employeeRepository.findById(employeeDto.getId())).thenReturn(Optional.of(mockedEmployee));
        employeeDto.setName("Rohit Simgh");
        employeeDto.setSalary(4223L);
        Employee newEmployee = modelMapper.map(employeeDto, Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

        //act
        EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(mockedEmployee.getId(), employeeDto);

        //assert
        assertThat(updatedEmployeeDto.getEmail()).isEqualTo(mockedEmployee.getEmail());
        verify(employeeRepository, atLeastOnce()).save(any());
    }

    @Test
    void testUpdateEmployeeById_WhenAttemptingToUpdateEmployeeNotPresentWithId_thenThrowException() {
        //Arrange
        when(employeeRepository.findById(employeeDto.getId())).thenReturn(Optional.empty());

        //Act + assert
        assertThatThrownBy(() -> employeeService.updateEmployee(1L, employeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("The employee not found here with id "+employeeDto.getId());
        verify(employeeRepository, atLeastOnce()).findById(1L);
        verify(employeeRepository, never()).save(any());
    }


    @Test
    void testDeleteEmployeeById_WhenEmployeeWithIdPresent_thenReturnTrue() {
        //arrange
        when(employeeRepository.existsById(employeeDto.getId())).thenReturn(true);

        //act
        boolean exists = employeeService.deleteEmployeeById(mockedEmployee.getId());

        //assert
//        assertThatCode(() -> employeeService.deleteEmployeeById(1L))
//                .doesNotThrowAnyException();
        assertThat(employeeDto.getId()).isEqualTo(mockedEmployee.getId());
        verify(employeeRepository).deleteById(employeeDto.getId());
    }

    @Test
    void testDeleteEmployeeById_whenEmployeeWithGivenIdNotPresent_thenReturnFalse() {
        //assert
        when(employeeRepository.existsById(employeeDto.getId())).thenReturn(false);

        //act + assert
        assertThatThrownBy(() -> employeeService.deleteEmployeeById(employeeDto.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id "+employeeDto.getId());

        verify(employeeRepository, atLeastOnce()).existsById(employeeDto.getId());
        verify(employeeRepository, never()).deleteById(employeeDto.getId());
    }

}