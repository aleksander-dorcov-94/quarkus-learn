package org.acme.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.enteties.Employee;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepository<Employee> {

}
