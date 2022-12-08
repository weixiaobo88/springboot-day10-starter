package com.rest.springbootemployee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.springbootemployee.entity.Company;
import com.rest.springbootemployee.entity.Employee;
import com.rest.springbootemployee.repository.CompanyMongoRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyControllerTest {
    @Autowired
    MockMvc client;

    @Autowired
    CompanyMongoRepository companyMongoRepository;

    @BeforeEach
    public void clearDB() {
        companyMongoRepository.deleteAll();
    }

    @Test
    public void should_get_all_companies_when_perform_get_given_two_employee() throws Exception {
        //given
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));
        companyMongoRepository.save(new Company(new ObjectId().toString(), "Spring", employees1));
        companyMongoRepository.save(new Company(new ObjectId().toString(), "Boot", employees2));

        //when & then
        client.perform(MockMvcRequestBuilders.get("/companies"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(2)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Spring"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employees[*].name", containsInAnyOrder("lili", "coco")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employees[*].age", containsInAnyOrder(20, 10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employees[*].gender", containsInAnyOrder("Female", "Female")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employees[*].salary", containsInAnyOrder(2000, 8000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Boot"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].employees[*].name", containsInAnyOrder("aaa", "bbb")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].employees[*].age", containsInAnyOrder(20, 10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].employees[*].gender", containsInAnyOrder("Male", "Male")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].employees[*].salary", containsInAnyOrder(2000, 8000)));
    }

    @Test
    public void should_get_right_company_when_perform_get_by_id_given_a_id() throws Exception {
        //given
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));
        Company company1 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Spring", employees1));
        Company company2 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Boot", employees2));

        //when & then
        client.perform(MockMvcRequestBuilders.get("/companies/{id}", company1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Spring"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].name", containsInAnyOrder("lili", "coco")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].age", containsInAnyOrder(20, 10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].gender", containsInAnyOrder("Female", "Female")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].salary", containsInAnyOrder(2000, 8000)));
    }

    @Test
    public void should_create_a_company_when_perform_post_given_a_company() throws Exception {
        //given
        String newCompanyJson = new ObjectMapper()
                .writeValueAsString(new Company(new ObjectId().toString(), "PPP", new ArrayList<Employee>() {{
                                    add(new Employee(String.valueOf(1), "lili", 20, "Female", 8000));
                                }}));

        //when & then
        client.perform(MockMvcRequestBuilders.post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCompanyJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("PPP"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].name").value("lili"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].age").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].gender").value("Female"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].salary").value(8000));

    }

    @Test
    public void should_get_updated_company_when_perform_put_by_id_given_a_id_and_a_company() throws Exception {
        //given
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));
        Company company1 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Spring", employees1));
        Company company2 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Boot", employees2));

        String newCompanyJson = new ObjectMapper().writeValueAsString(new Company(new ObjectId().toString(), "TETE", null));

        //when & then
        client.perform(MockMvcRequestBuilders.put("/companies/{id}", company1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCompanyJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(company1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TETE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].name", containsInAnyOrder("lili", "coco")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].age", containsInAnyOrder(20, 10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].gender", containsInAnyOrder("Female", "Female")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].salary", containsInAnyOrder(2000, 8000)));
    }

    @Test
    public void should_delete_a_company_when_perform_delete_by_id_given_a_id() throws Exception {
        //given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(new ObjectId().toString(), "lili", 20, "Female", 2000));
        employees.add(new Employee(new ObjectId().toString(), "coco", 10, "Female", 8000));

        Company company = companyMongoRepository.save(new Company(new ObjectId().toString(), "Spring", employees));

        //when & then
        client.perform(MockMvcRequestBuilders.delete("/companies/{id}", company.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void should_get_right_two_companies_when_perform_get_by_page_given_5_companies_and_page_2_and_page_size_2() throws Exception {
        //given
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));

        List<Employee> employees3 = new ArrayList<>();
        employees3.add(new Employee(String.valueOf(5), "ccc", 20, "Female", 2000));
        employees3.add(new Employee(String.valueOf(6), "ddd", 10, "Female", 8000));

        List<Employee> employees4 = new ArrayList<>();
        employees4.add(new Employee(String.valueOf(7), "eee", 20, "Male", 2000));
        employees4.add(new Employee(String.valueOf(8), "fff", 10, "Male", 8000));

        Company company1 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Spring", employees1));
        Company company2 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Boot", employees2));
        Company company3 = companyMongoRepository.save(new Company(new ObjectId().toString(), "TET", employees3));
        Company company4 = companyMongoRepository.save(new Company(new ObjectId().toString(), "POP", employees4));

        int page = 2;
        int pageSize = 2;

        //when & then
        client.perform(MockMvcRequestBuilders.get("/companies?page={page}&pageSize={pageSize}", page, pageSize))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(company3.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("TET"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employees[*].name", containsInAnyOrder("ccc", "ddd")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employees[*].age", containsInAnyOrder(20, 10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employees[*].gender", containsInAnyOrder("Female", "Female")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employees[*].salary", containsInAnyOrder(2000, 8000)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(company4.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("POP"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].employees[*].name", containsInAnyOrder("eee", "fff")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].employees[*].age", containsInAnyOrder(20, 10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].employees[*].gender", containsInAnyOrder("Male", "Male")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].employees[*].salary", containsInAnyOrder(2000, 8000)));
    }

    @Test
    public void should_get_employees_when_perform_get_by_id_given_companies() throws Exception {
        //given
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));

        List<Employee> employees3 = new ArrayList<>();
        employees3.add(new Employee(String.valueOf(5), "ccc", 20, "Female", 2000));
        employees3.add(new Employee(String.valueOf(6), "ddd", 10, "Female", 8000));

        List<Employee> employees4 = new ArrayList<>();
        employees4.add(new Employee(String.valueOf(7), "eee", 20, "Male", 2000));
        employees4.add(new Employee(String.valueOf(8), "fff", 10, "Male", 8000));

        Company company1 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Spring", employees1));
        Company company2 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Boot", employees2));
        Company company3 = companyMongoRepository.save(new Company(new ObjectId().toString(), "TET", employees3));
        Company company4 = companyMongoRepository.save(new Company(new ObjectId().toString(), "POP", employees4));

        String id = company3.getId();

        //when & then
        client.perform(MockMvcRequestBuilders.get("/companies/{id}/employees", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name", containsInAnyOrder("ccc", "ddd")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].age", containsInAnyOrder(20, 10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].gender", containsInAnyOrder("Female", "Female")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].salary", containsInAnyOrder(2000, 8000)));
    }
}
