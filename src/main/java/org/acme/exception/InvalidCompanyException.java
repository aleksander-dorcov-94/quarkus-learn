package org.acme.exception;

public class InvalidCompanyException extends RuntimeException
{
  public InvalidCompanyException(String message)
  {
    super(message);
  }
}
