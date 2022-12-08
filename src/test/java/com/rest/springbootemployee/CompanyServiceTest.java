package com.rest.springbootemployee;

import com.rest.springbootemployee.entity.Company;
import com.rest.springbootemployee.entity.Employee;
import com.rest.springbootemployee.repository.CompanyMongoRepository;
import com.rest.springbootemployee.service.CompanyService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class CompanyServiceTest {

    @Mock
    CompanyMongoRepository companyMongoRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    public void should_return_all_companies_when_find_all_given_companies(){
        //given
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));

        Company company1 = new Company(new ObjectId().toString(), "Spring", employees1);
        Company company2 = new Company(new ObjectId().toString(), "Boot", employees2);

        List<Company> companies = new ArrayList<>(Arrays.asList(company1,company2));

        given(companyMongoRepository.findAll()).willReturn(companies);

        //when
        List<Company> actualCompanies = companyService.findAll();

        //then
        assertThat(actualCompanies, hasSize(2));
        assertThat(actualCompanies.get(0), equalTo(company1));
        assertThat(actualCompanies.get(1), equalTo(company2));
    }

    @Test
    public void should_return_company_when_update_given_a_company(){
        //given
        String companyName = "POL";
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));

        Company originalCompany = new Company(new ObjectId().toString(), "Spring", employees1);
        Company toUpdateCompany = new Company(new ObjectId().toString(), companyName, employees2);

        String id = originalCompany.getId();
        given(companyMongoRepository.findById(id)).willReturn(Optional.of(originalCompany));
        given(companyMongoRepository.save(originalCompany)).willReturn(toUpdateCompany);

        //when
        Company actualCompany = companyService.update(id, toUpdateCompany);

        //then
        verify(companyMongoRepository).findById(id);
        assertThat(actualCompany.getName(), equalTo(companyName));
    }

    @Test
    public void should_return_a_right_company_when_find_by_id_given_a_id(){
        // given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        Company company = new Company(new ObjectId().toString(), "Spring", employees);
        String id = company.getId();

        given(companyMongoRepository.findById(id)).willReturn(Optional.of(company));

        // when
        Company actualCompany = companyService.findById(id);

        // then
        assertThat(actualCompany, equalTo(company));
    }

    @Test
    public void should_return_a_company_when_add_given_a_company(){
        // given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        Company originalCompany = new Company(new ObjectId().toString(), "Spring", employees);

        Company createdCompany = new Company(new ObjectId().toString(), "Spring", employees);

        given(companyMongoRepository.save(originalCompany)).willReturn(createdCompany);

        // when
        Company actualCompany = companyService.create(originalCompany);

        // then
        assertThat(actualCompany, equalTo(createdCompany));
        verify(companyMongoRepository).save(originalCompany);
    }
    @Test
    public void should_delete_a_company_when_delete_given_a_id(){
        //given
        final String companyId = new ObjectId().toString();

        //when
        companyService.delete(companyId);

        //then
        verify(companyMongoRepository).deleteById(companyId);
    }

    @Test
    public void should_return_two_right_companies_when_find_by_page_given_5_companies_and_page_2_and_page_size_2(){
        //given
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));

        List<Employee> employees3 = new ArrayList<>();
        employees3.add(new Employee(String.valueOf(5), "lili", 20, "Female", 2000));
        employees3.add(new Employee(String.valueOf(6), "coco", 10, "Female", 8000));

        List<Employee> employees4 = new ArrayList<>();
        employees4.add(new Employee(String.valueOf(7), "aaa", 20, "Male", 2000));
        employees4.add(new Employee(String.valueOf(8), "bbb", 10, "Male", 8000));

        Company company1 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Spring", employees1));
        Company company2 = companyMongoRepository.save(new Company(new ObjectId().toString(), "Boot", employees2));

        List<Company> companies = new ArrayList<>(Arrays.asList(company1,company2));

        int page = 2;
        int pageSize = 2;

        given(companyMongoRepository.findAll(PageRequest.of(page-1, pageSize))).willReturn(new PageImpl<>(companies));

        //when
        List<Company> actualCompanies = companyService.findByPage(page, pageSize);

        //then
        assertThat(actualCompanies, hasSize(2));
        assertThat(actualCompanies.get(0), equalTo(company1));
        assertThat(actualCompanies.get(1), equalTo(company2));
    }

    @Test
    public void should_return_employees_when_find_employees_by_company_id_given_a_id(){
        //given
        Employee employee1 = new Employee(String.valueOf(1), "lili", 20, "Female", 2000);
        Employee employee2 = new Employee(String.valueOf(2), "coco", 10, "Female", 8000);
        List<Employee> employees = new ArrayList<>(Arrays.asList(employee1, employee2));

        Company company = new Company(new ObjectId().toString(), "Spring", employees);
        String id = company.getId();

        given(companyMongoRepository.findById(id)).willReturn(Optional.of(company));

        //when
        List<Employee> actualEmployees = companyService.getEmployees(id);

        //then
        assertThat(actualEmployees, hasSize(2));
        assertThat(actualEmployees.get(0), equalTo(employee1));
        assertThat(actualEmployees.get(1), equalTo(employee2));
    }
}
