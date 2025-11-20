package co.com.bancolombia.api.exceptionhandler;

import co.com.bancolombia.model.exception.UserNotFoundException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebInputException;

@Getter
public enum ExceptionMapping {
  USER_NOT_FOUND(UserNotFoundException.class, HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"),
  SERVER_WEB_INPUT_ERROR(ServerWebInputException.class, HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Malformed request body"),
  GENERAL_ERROR(Exception.class, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Internal server error");

  private final Class<? extends Throwable> exceptionClass;
  private final HttpStatus status;
  private final String responseCode;
  private final String message;

  ExceptionMapping(Class<? extends Throwable> exceptionClass, HttpStatus status, String responseCode, String message) {
    this.exceptionClass = exceptionClass;
    this.status = status;
    this.responseCode = responseCode;
    this.message = message;
  }

  public static ExceptionMapping from(Throwable ex) {
    for (ExceptionMapping mapping : values()) {
      if (mapping.exceptionClass.isAssignableFrom(ex.getClass())) {
        return mapping;
      }
    }
    return GENERAL_ERROR;
  }

  public String getMessageFor(Throwable ex) {
    if (this == GENERAL_ERROR) {
      return this.message;
    }
    return (ex.getMessage() != null && !ex.getMessage().isEmpty()) ? ex.getMessage() : this.message;
  }
}