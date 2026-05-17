package org.acme.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.enteties.Address;

@ApplicationScoped
public class AddressRepository implements PanacheRepository<Address> {

}
