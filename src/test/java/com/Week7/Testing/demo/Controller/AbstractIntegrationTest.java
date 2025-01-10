package com.Week7.Testing.demo.Controller;


import com.Week7.Testing.demo.Dto.EmployeeDto;
import com.Week7.Testing.demo.Entity.Employee;
import com.Week7.Testing.demo.TestContainerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@Import(TestContainerConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    Employee testEmployee = Employee.builder()
            .id(1L)
                .name("Rohit Bisht")
                .email("rohitbisht0911@gmail.com")
                .salary(89880L)
                .build();

    EmployeeDto testEmployeeeDto = EmployeeDto.builder()
            .id(1L)
                .name("Rohit Bisht")
                .email("rohitbisht0911@gmail.com")
                .salary(89880L)
                .build();


    //Placed every repreated code here so that it can be easily used with other controllers also by just extending this class
}
