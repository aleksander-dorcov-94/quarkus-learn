package org.acme.rest.advice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.acme.exception.InvalidCompanyException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final UriInfo uriInfo;
    private final Request request;

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleInvalidCompany(InvalidCompanyException ex) {

        log.info(request.getMethod() + " " + uriInfo.getAbsolutePath() + " " + ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .status(422)
                .error("Business Rule Violation")
                .message(ex.getMessage())
                .path(uriInfo.getPath())
                .build();

        return RestResponse.status(Response.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(Response.Status.BAD_REQUEST.getStatusCode())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(uriInfo.getPath())
                .build();

        return RestResponse.status(Response.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(Response.Status.BAD_REQUEST.getStatusCode())
                .error("Validation Failed")
                .message(ex.getMessage())
                .path(uriInfo.getPath())
                .build();

        return RestResponse.status(Response.Status.BAD_REQUEST, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(Response.Status.NOT_FOUND.getStatusCode())
                .error("Not Found")
                .message(ex.getMessage() != null ? ex.getMessage() : "The requested resource was not found")
                .path(uriInfo.getPath())
                .build();

        return RestResponse.status(Response.Status.NOT_FOUND, error);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .error("Internal Server Error")
                .message("An unexpected error occurred: " + ex.getMessage())
                .path(uriInfo.getPath())
                .build();

        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, error);
    }
}
