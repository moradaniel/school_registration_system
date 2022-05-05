package com.registration.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.util.Set;

@Component
public class StudentCreateDtoValidator implements Validator {

    @Autowired
    private MessageSource messageSource;

    private final javax.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public StudentCreateDtoValidator() {
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StudentDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        StudentDto studentDto = (StudentDto) target;

        Set<ConstraintViolation<StudentDto>> violations = validator.validate(studentDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

    }
}