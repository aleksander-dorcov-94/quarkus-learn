package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.acme.domain.enteties.Address;
import org.acme.domain.enteties.Company;
import org.acme.domain.enteties.Employee;
import org.acme.domain.repositories.AddressRepository;
import org.acme.domain.repositories.CompanyRepository;
import org.acme.domain.repositories.EmployeeRepository;
import org.acme.exception.InvalidCompanyException;
import org.acme.rest.dto.CreateAddressRequest;
import org.acme.rest.dto.CreateCompanyRequest;
import org.acme.rest.dto.CreateEmployeeRequest;

import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final AddressRepository addressRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Company createCompany(CreateCompanyRequest request) {
        throwExceptionOnMagicSuffix(request);
        Company company = new Company();
        company.setName(request.name());

        companyRepository.persist(company);
        return company;
    }

    @Transactional
    public Optional<Address> addAddress(CreateAddressRequest request) {
        Company companyProxy = companyRepository
                .getEntityManager()
                .getReference(Company.class, request.companyId());

        Address address = new Address();
        address.setCity(request.city());
        address.setStreet(request.street());
        address.setCompany(companyProxy);

        addressRepository.persist(address);

        return Optional.of(address);
    }

    @Transactional
    public Optional<Employee> addEmployee(CreateEmployeeRequest request) {
        Company company = companyRepository.getEntityManager()
                .getReference(Company.class, request.companyId());
        if (company == null) {
            return Optional.empty();
        }

        Employee employee = new Employee();
        employee.setName(request.name());
        employee.setCompany(company);

        employeeRepository.persist(employee);

        return Optional.of(employee);
    }

    private void throwExceptionOnMagicSuffix(CreateCompanyRequest request) {
        String name = request.name();

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (name.endsWith("Ltd")) {
            throw new InvalidCompanyException("We don't do Limited companies.");
        } else if (name.endsWith("Inc")) {
            throw new IllegalArgumentException("Incorporate entities are not allowed.");
        } else if (name.endsWith("!")) {
            throw new ValidationException("Company names cannot be exciting!");
        }
    }
}
