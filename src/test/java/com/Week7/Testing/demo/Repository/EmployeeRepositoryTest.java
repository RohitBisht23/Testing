package com.Week7.Testing.demo.Repository;

import com.Week7.Testing.demo.Entity.Employee;
//import com.Week7.Testing.demo.TestContainerConfiguration;
import com.Week7.Testing.demo.TestContainerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(TestContainerConfiguration.class)
@DataJpaTest
//@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                //.id(1L)
                .name("Rohit")
                .email("rohitbisht0911@gmail.com")
                .salary(100000L)
                .build();
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployee() {
        //Arrange, Give
        employeeRepository.save(employee);

        //Act, When
        List<Employee> employeeList = employeeRepository.findByEmail(employee.getEmail());

        //Assert, Then
        assertThat(employeeList).isNotNull();
        //assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.get(0).getEmail()).isEqualTo(employee.getEmail());
        System.out.println(employeeList.get(0).getEmail());
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_ThenReturnEmptyEmployeeList() {
        //Given
            String email = "notPresent123@gmail.com";

        //When
        List<Employee> employeeList = employeeRepository.findByEmail(email);

        //Then
        assertThat(employeeList).isNotNull();
        //assertThat(employeeList).isEmpty();
    }
}