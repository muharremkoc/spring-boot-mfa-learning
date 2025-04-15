package com.spring.springbootmfalearning.exceptions;

import com.spring.springbootmfalearning.model.http.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.naming.ServiceUnavailableException;
import java.util.Collections;


@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(value = { UserAlreadyExistException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?>  handleUserAlreadyExistException(Exception ex,WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(value = { UserNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?>  handleUserNotFoundException(Exception ex,WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }


    @ExceptionHandler(value = { BadCredentialsException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?>  handleBadCredentialsException(Exception ex,WebRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(value = { AuthenticationException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?>  handleAuthenticationException(Exception ex,WebRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(value = { ForbiddenException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?>  handleForbiddenException(Exception ex,WebRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?>  handleAccessDeniedException(Exception ex,WebRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?>  handleConflictExceptionException(ConflictException ex,WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(value = {ServiceUnavailableException.class})
    public ResponseEntity<?>  handleServiceUnavailableException(ServiceUnavailableException ex,WebRequest request) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?>  handleValidationErrors(MethodArgumentNotValidException ex,WebRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }


    private ResponseEntity<ApiErrorResponse<Object>> buildResponse(
            HttpStatus httpStatus, String message, WebRequest request) {

        var response = new ApiErrorResponse<>();
        response.setHttpStatus(httpStatus);
        response.setStatusCode(httpStatus.value());
        response.setPath(request.getDescription(false));
        response.setErrors(Collections.singletonList(message));

        log.error(response.toString());

        return ResponseEntity.status(httpStatus).body(response);
    }


}