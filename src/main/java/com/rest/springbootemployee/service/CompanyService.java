package com.rest.springbootemployee.service;

import com.rest.springbootemployee.entity.Company;
import com.rest.springbootemployee.exception.NoCompanyFoundException;
import com.rest.springbootemployee.repository.CompanyMongoRepository;
import com.rest.springbootemployee.entity.Employee;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    private CompanyMongoRepository companyMongoRepository;

    public CompanyService(CompanyMongoRepository companyMongoRepository) {
        this.companyMongoRepository = companyMongoRepository;
    }

    public List<Company> findAll() {
        return companyMongoRepository.findAll();
    }

    public List<Company> findByPage(Integer page, Integer pageSize) {
        return companyMongoRepository.findAll(PageRequest.of(page-1, pageSize)).toList();
    }


    public Company findById(String companyId) {
        return companyMongoRepository.findById(companyId).orElseThrow(NoCompanyFoundException::new);
    }

    public Company create(Company company) {
        return companyMongoRepository.save(company);
    }

    public void delete(String companyId) {
        companyMongoRepository.deleteById(companyId);
    }

    public Company update(String companyId, Company toUpdateCompany) {
        Company existingCompany = companyMongoRepository.findById(companyId)
                .orElseThrow(NoCompanyFoundException::new);
        if (toUpdateCompany.getName() != null) {
            existingCompany.setName(toUpdateCompany.getName());
        }
        return companyMongoRepository.save(existingCompany);
    }

    public List<Employee> getEmployees(String companyId) {
        Company company = companyMongoRepository.findById(companyId)
                .orElseThrow(NoCompanyFoundException::new);
        return company.getEmployees();
    }

}
