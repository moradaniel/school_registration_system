package com.registration.util;

import java.util.List;

/**
 * Represents a core service operation result.
 * @param <T>
 */
public class Result<T> {

    private T target;
    List<String> errors;

    public Result(T target){
        this.target = target;
    }

    public Result(T target, List<String> errors) {
        this(target);
        this.errors = errors;
    }

    public T getTarget() {
        return target;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean isOk(){
        return errors ==null || errors.isEmpty();
    }
}
