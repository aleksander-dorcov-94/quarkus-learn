package org.acme.rest.logging;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoggingFilter {

    @ServerRequestFilter
    public void logRequest(ContainerRequestContext requestContext) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        String method = requestContext.getMethod();
        String path = uriInfo.getPath();

        InputStream entityStream = requestContext.getEntityStream();

        if (entityStream != null) {
            byte[] bytes = entityStream.readAllBytes();
            String body = new String(bytes, StandardCharsets.UTF_8);

            if (!body.isBlank()) {
                log.info("--> {} {} | Body: {}", method, path, body);
            } else {
                log.info("--> {} {}", method, path);
            }

            requestContext.setEntityStream(new ByteArrayInputStream(bytes));
        } else {
            log.info("--> {} {}", method, path);
        }
    }

    @ServerResponseFilter
    public void logResponse(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        UriInfo uriInfo = requestContext.getUriInfo();
        String path = uriInfo.getPath();
        int status = responseContext.getStatus();

        log.info("<-- {} {}", status, path);
    }
}
