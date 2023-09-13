package org.blbulyandavbulyan.blog.controllers.handling;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.blbulyandavbulyan.blog.dtos.error.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ValidationControllerAdvice {
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> processConstraintViolationException(ConstraintViolationException processedException) {
        Map<String, List<String>> errorsDescriptions = new HashMap<>();
        List<ConstraintViolation<?>> unprocessedViolations = new ArrayList<>();
        for (var constraintViolation : processedException.getConstraintViolations()) {
            if (constraintViolation.getRootBeanClass().isAnnotationPresent(RestController.class)) {
                for (Path.Node node : constraintViolation.getPropertyPath()) {
                    if (node.getKind() == ElementKind.PARAMETER) {
                        errorsDescriptions.computeIfAbsent(node.getName(), k -> new ArrayList<>()).add(constraintViolation.getMessage());
                    }
                }
            } else unprocessedViolations.add(constraintViolation);
        }
        if (!unprocessedViolations.isEmpty())
            log.error("Unprocessed constrain violation detected: {}", unprocessedViolations);
        return ResponseEntity.badRequest().body(new ValidationError(HttpStatus.BAD_REQUEST.value(), "Validation error", errorsDescriptions));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> processMethodArgumentNotValidException(MethodArgumentNotValidException processedException){
            BindingResult bindingResult = processedException.getBindingResult();
            Map<String, List<String>> fieldErrors = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                fieldErrors.computeIfAbsent(fieldError.getField(), (key)->new ArrayList<>()).add(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(new ValidationError(processedException.getStatusCode().value(),
                    "Validation error", fieldErrors), processedException.getStatusCode());
    }
}
