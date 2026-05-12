package org.acme.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEmployeeRequest(
  @NotBlank(message = "Employee name is required")
  String name,

  @NotNull(message = "Employee must belong to a company")
  Long companyId
) {}
