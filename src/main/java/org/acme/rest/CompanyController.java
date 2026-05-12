package org.acme.rest;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.domain.enteties.Address;
import org.acme.domain.enteties.Company;
import org.acme.domain.enteties.Employee;
import org.acme.rest.dto.CreateAddressRequest;
import org.acme.rest.dto.CreateCompanyRequest;
import org.acme.rest.dto.CreateEmployeeRequest;
import org.acme.service.CompanyService;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
public class CompanyController {

  @Inject
  CompanyService companyService;

  @POST
  public RestResponse<Company> createCompany(@Valid CreateCompanyRequest request) {
    Company company = companyService.createCompany(request);
    return RestResponse.status(Response.Status.CREATED, company);
  }

  @POST
  @Path("/address")
  public RestResponse<Address> addAddress(@Valid CreateAddressRequest request) {
    return companyService.addAddress(request)
      .map(address -> RestResponse.status(Response.Status.CREATED, address))
      .orElse(RestResponse.status(Response.Status.NOT_FOUND));
  }

  @POST
  @Path("/employee")
  public RestResponse<Employee> addEmployee(@Valid CreateEmployeeRequest request) {
    return companyService.addEmployee(request)
      .map(employee -> RestResponse.status(Response.Status.CREATED, employee))
      .orElse(RestResponse.status(Response.Status.NOT_FOUND));
  }
}
