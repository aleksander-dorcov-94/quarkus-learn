package org.acme.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAddressRequest(@NotNull(message = "Company ID is required to attach the address") Long companyId,
                                   @NotBlank(message = "City cannot be blank") String city,
                                   @NotBlank(message = "Street cannot be blank") String street) {

}
