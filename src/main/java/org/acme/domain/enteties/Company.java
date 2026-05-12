package org.acme.domain.enteties;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
public class Company extends PanacheEntityBase
{

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
  @SequenceGenerator(name = "company_seq", sequenceName = "company_seq", allocationSize = 1)
  private Long id;

  private String name;

  @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
  private Address address;

  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Employee> employees = new ArrayList<>();
}
