package com.registration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiResponse<T> {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errors = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T response;

    public ApiResponse() {
    }

    public ApiResponse(T response) {
        this.response = response;
    }
    public ApiResponse(T response, List<String> errors) {
        this(response);
        this.errors.addAll(errors);
    }

    public ApiResponse(List<String> errors) {
        this(null, errors);
    }


    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }


    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

}
