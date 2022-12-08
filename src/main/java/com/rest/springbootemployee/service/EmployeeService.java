package com.rest.springbootemployee.service;

import com.rest.springbootemployee.entity.Employee;
import com.rest.springbootemployee.exception.NoEmployeeFoundException;
import com.rest.springbootemployee.repository.EmployeeMongoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {// SUT

    private EmployeeMongoRepository employeeMongoRepository;

    public EmployeeService(EmployeeMongoRepository employeeMongoRepository) {
        this.employeeMongoRepository = employeeMongoRepository;
    }

    public List<Employee> findAll() {
        return employeeMongoRepository.findAll();
    }

    public Employee update(String id, Employee employee) {
        Employee existingEmployee = employeeMongoRepository.findById(id)
                .orElseThrow(NoEmployeeFoundException::new);
        if (employee.getAge() != null) {
            existingEmployee.setAge(employee.getAge());
        }
        if (employee.getSalary() != null) {
            existingEmployee.setSalary(employee.getSalary());
        }
        return employeeMongoRepository.save(existingEmployee);
    }

    public Employee findById(String id) {
        return employeeMongoRepository.findById(id)
                .orElseThrow(NoEmployeeFoundException::new);
    }

    public List<Employee> findByGender(String gender) {
        return employeeMongoRepository.findByGender(gender);
    }

    public List<Employee> findByPage(int page, int pageSize) {
        return employeeMongoRepository
                .findAll(PageRequest.of(page-1, pageSize))
                .toList();
    }

    public void delete(String id) {
        employeeMongoRepository.deleteById(id);
    }

    public Employee create(Employee employee) {
        return employeeMongoRepository.save(employee);
    }
}
