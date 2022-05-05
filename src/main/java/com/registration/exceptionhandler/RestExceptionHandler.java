package com.registration.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Catch Exceptions and build a message for the REST client
 *
 */


@RestControllerAdvice
public class RestExceptionHandler {

    public static final String MESSAGE = "message";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler(org.springframework.web.bind.MissingPathVariableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingPathVariableException(HttpServletRequest request, org.springframework.web.bind.MissingPathVariableException e) {
        HashMap<String, String> response = new HashMap<>();
        response.put(MESSAGE, "Required path variable is missing in this request. "+e.getMessage());
        return response;
    }


    @ExceptionHandler(com.fasterxml.jackson.databind.exc.InvalidFormatException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidFormatException(HttpServletRequest request, InvalidFormatException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put(MESSAGE, "Malformed request. "+ex.getMessage());
        return response;
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put(MESSAGE, "Malformed request. "+ex.getMessage());
        return response;
    }


    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid( WebRequest request, MethodArgumentNotValidException ex) {

        ApiError apiError = new ApiError();
        apiError.setCount(ex.getBindingResult().getErrorCount());
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setError("Validation failed");

        List<String> errors =
                ex.getBindingResult().getAllErrors()
                        .stream()
                        .map(this::mapError)
                        .collect(Collectors.toList());

        apiError.setErrors(errors);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getSimpleName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ApiError apiError = new ApiError();
        apiError.setCount(errors.size());
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setError("Validation failed");

        apiError.setErrors(errors);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private String mapError(ObjectError objectError) {
        if (objectError instanceof FieldError) {
            return ((FieldError) objectError).getField() +" "+ objectError.getDefaultMessage();
        }
        return objectError.getObjectName() +" "+ objectError.getDefaultMessage();
    }
}