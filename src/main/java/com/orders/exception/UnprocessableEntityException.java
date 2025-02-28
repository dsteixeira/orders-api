package com.orders.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class UnprocessableEntityException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 5120698653021961500L;

  private final String code;
  private final Object[] args;
  private final HttpStatus status;

  public UnprocessableEntityException(String code, Object... args) {
    this.code = code;
    this.args = args;
    this.status = HttpStatus.UNPROCESSABLE_ENTITY;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }

  public Object[] getArgs() {
    return args;
  }
}
