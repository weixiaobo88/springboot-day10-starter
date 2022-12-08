package com.rest.springbootemployee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.springbootemployee.entity.Employee;
import com.rest.springbootemployee.repository.EmployeeMongoRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {
    @Autowired
    MockMvc client;

    @Autowired
    EmployeeMongoRepository employeeMongoRepository;

    @BeforeEach
    void cleanRepository() {
        employeeMongoRepository.deleteAll();
    }

    @Test
    void should_get_all_employees_when_perform_get_given_employees() throws Exception {
        //given
        String employeeId = new ObjectId().toString();
        employeeMongoRepository.save(new Employee(employeeId, "Susan", 22, "Female", 10000));

        //when & then
        client.perform(MockMvcRequestBuilders.get("/employees"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Susan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(22))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value("Female"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(10000));
    }

    @Test
    void should_get_employee_by_id_when_perform_get_by_id_given_employees() throws Exception {
        //given
        String employeeId = new ObjectId().toString();
        Employee susan = employeeMongoRepository.save(new Employee(employeeId, "Susan", 22, "Female", 10000));
        employeeMongoRepository.save(new Employee(new ObjectId().toString(), "Bob", 23, "Male", 9000));

        //when & then
        client.perform(MockMvcRequestBuilders.get("/employees/{id}", employeeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Susan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(22))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value("Female"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(10000));
    }

    @Test
    void should_return_employees_when_perform_get_by_gender_given_employees() throws Exception {
        //given
        String employeeId = new ObjectId().toString();
        employeeMongoRepository.save(new Employee(employeeId, "Susan", 22, "Female", 10000));
        employeeMongoRepository.save(new Employee(new ObjectId().toString(), "Leo", 25, "Male", 9000));
        employeeMongoRepository.save(new Employee(new ObjectId().toString(), "Robert", 20, "Male", 8000));

        //when & then
        client.perform(MockMvcRequestBuilders.get("/employees?gender={gender}", "Male")) // http status 200
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", containsInAnyOrder("Leo", "Robert")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].age", containsInAnyOrder( 20, 25)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].gender", containsInAnyOrder( "Male", "Male")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].salary", containsInAnyOrder( 9000, 8000)));
    }

    @Test
    void should_return_employees_when_perform_get_by_page_given_employees() throws Exception {
        //given
        String employeeId = new ObjectId().toString();
        employeeMongoRepository.save(new Employee(employeeId, "Susan", 22, "Female", 10000));
        employeeMongoRepository.save(new Employee(new ObjectId().toString(), "Leo", 25, "Male", 9000));
        employeeMongoRepository.save(new Employee(new ObjectId().toString(), "Robert", 20, "Male", 8000));

        //when & then
        client.perform(MockMvcRequestBuilders.get("/employees?page={page}&pageSize={pageSize}", 1, 2)) // http status 200
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", containsInAnyOrder("Susan", "Leo")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].age", containsInAnyOrder(22, 25)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].salary", containsInAnyOrder(9000, 10000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].gender", containsInAnyOrder("Female", "Male")));
    }

    @Test
    void should_return_updated_employee_when_perform_put_given_employee() throws Exception {
        //given
        String employeeId = new ObjectId().toString();
        Employee employee = employeeMongoRepository.save(new Employee(employeeId, "Susan", 22, "Female", 10000));
        Employee updateEmployee = new Employee(employeeId, "Jim", 20, "Male", 55000);

        String updateEmployeeJson = new ObjectMapper().writeValueAsString(updateEmployee);

        //when
        client.perform(MockMvcRequestBuilders.put("/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEmployeeJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Susan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(55000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value("Female"));

        // then
        final Employee updatedEmployee = employeeMongoRepository.findAll().get(0);
        assertThat(updatedEmployee.getName(), equalTo("Susan"));
        assertThat(updatedEmployee.getAge(), equalTo(20));
        assertThat(updatedEmployee.getSalary(), equalTo(55000));
        assertThat(updatedEmployee.getGender(), equalTo("Female"));

    }

    @Test
    void should_create_new_employee_when_perform_post_given_new_employee() throws Exception {
        //given
        String employeeId = new ObjectId().toString();
        Employee newEmployee = new Employee(employeeId, "Jim", 20, "Male", 55000);
        String newEmployeeJson = new ObjectMapper().writeValueAsString(newEmployee);

        //when
        client.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmployeeJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jim"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(55000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value("Male"));

        // then
        List<Employee> employees = employeeMongoRepository.findAll();
        assertThat(employees, hasSize(1));
        assertThat(employees.get(0).getName(), equalTo("Jim"));
        assertThat(employees.get(0).getAge(), equalTo(20));
        assertThat(employees.get(0).getSalary(), equalTo(55000));
        assertThat(employees.get(0).getGender(), equalTo("Male"));

    }

    @Test
    void should_return_204_when_perform_delete_given_employee() throws Exception {
        //given
        String employeeId = new ObjectId().toString();
        Employee createdEmployee = employeeMongoRepository.save(new Employee(employeeId, "Jim", 20, "Male", 55000));

        //when
        client.perform(MockMvcRequestBuilders.delete("/employees/{id}" , employeeId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        //then
        assertThat(employeeMongoRepository.findAll(), empty());
    }


}
