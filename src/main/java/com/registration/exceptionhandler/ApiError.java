package com.registration.exceptionhandler;

import java.io.Serializable;
import java.util.List;
import org.springframework.http.HttpStatus;

public class ApiError implements Serializable {
    private static final long serialVersionUID = 1L;

    private HttpStatus status;
    private String error;
    private Integer count;
    private List<String> errors;

    public ApiError() {
        //
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}