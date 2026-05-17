package org.acme.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.enteties.Company;

@ApplicationScoped
public class CompanyRepository implements PanacheRepository<Company> {

}
