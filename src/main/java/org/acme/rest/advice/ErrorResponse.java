package org.acme.rest.advice;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(int status,
                            String error,
                            String message,
                            String path,
                            LocalDateTime timestamp)
{
  public static ErrorResponseBuilder builder()
  {
    return new ErrorResponseBuilder().timestamp(LocalDateTime.now());
  }
}
