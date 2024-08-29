package com.harena.api.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends ApiException {
  public InternalServerErrorException(String message) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public InternalServerErrorException() {
    super("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
