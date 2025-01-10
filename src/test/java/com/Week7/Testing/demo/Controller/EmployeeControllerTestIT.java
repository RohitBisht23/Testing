package com.Week7.Testing.demo.Controller;

import com.Week7.Testing.demo.Dto.EmployeeDto;
import com.Week7.Testing.demo.Entity.Employee;
import com.Week7.Testing.demo.Repository.EmployeeRepository;
import com.Week7.Testing.demo.TestContainerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;



@AutoConfigureWebTestClient
@Import(TestContainerConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTestIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;


    private Employee testEmployee;
    private EmployeeDto testEmployeeeDto;

    @BeforeEach
    void setUp() {
        testEmployee = Employee.builder()
                .id(1L)
                .name("Rohit Bisht")
                .email("rohitbisht0911@gmail.com")
                .salary(89880L)
                .build();

        testEmployeeeDto = EmployeeDto.builder()
                .id(1L)
                .name("Rohit Bisht")
                .email("rohitbisht0911@gmail.com")
                .salary(89880L)
                .build();

        employeeRepository.deleteAll(); //Keep deleting the employee repository
        //testEmployeeeDto = modelMapper.map(employee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_success() {
        //Save employee
        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.get()
                .uri("/employee/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedEmployee.getId())
                .jsonPath("$.email").isEqualTo(savedEmployee.getEmail());
                 //test in whole thing together
//                .value(employeeDto -> {   //Test in multiple fields
//                    assertThat(employeeDto.getId()).isEqualTo(savedEmployee.getId());
//                    assertThat(employeeDto.getEmail()).isEqualTo(savedEmployee.getEmail());
//                });
    }


    @Test
    void testGetEmployeeById_Failure() {
        webTestClient.get()
                .uri("/employee/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        webTestClient.post()
                .uri("/employee/newEmployee")
                .bodyValue(testEmployeeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeNotExists_thenCreateEmployee() {
        webTestClient.post()
                .uri("/employee/newEmployee")
                .bodyValue(testEmployeeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeeDto.getEmail())
                .jsonPath("$.name").isEqualTo(testEmployeeeDto.getName());
    }

    @Test
    void testGetAllEmployee() {
        webTestClient.get()
                .uri("/employee")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testUpdateEmployeeById_whenEmployeeDoesNotExists_thenThrowException() {
        webTestClient.put()
                .uri("/employee/999")
                .bodyValue(testEmployeeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeeDto.setName("Ram singh");
        testEmployeeeDto.setEmail("rohit@gmail.com");

        webTestClient.put()
                .uri("/employee/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
        }

    @Test
    void testEmployeeById_whenEmployeePresentWithId_thenUpdateEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeeDto.setName("Ram Singh");
        testEmployeeeDto.setSalary(98000L);
        testEmployeeeDto.setId(savedEmployee.getId());

        webTestClient.put()
                .uri("/employee/{id}", savedEmployee.getId())
                .bodyValue(testEmployeeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .isEqualTo(testEmployeeeDto);
    }

    @Test
    void testDeleteEmployeeById_WhenIdNotPresent_thenthrowException() {
        webTestClient.delete()
                .uri("employee/999")
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void testDeleteEmployeeById_whenEmployeeFoundWithId_thenDeleteAndReturnTrue() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.delete()
                .uri("/employee/{id}",savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);

        //Again calling webtestclient for the same id to check id successfully deleted then we should get the exception
        webTestClient.delete()
                .uri("employee/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNotFound();

    }
}