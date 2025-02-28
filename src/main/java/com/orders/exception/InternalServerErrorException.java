package com.orders.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class InternalServerErrorException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -7581172110289638968L;

  private final HttpStatus status;

  public InternalServerErrorException() {
    this.status = HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
