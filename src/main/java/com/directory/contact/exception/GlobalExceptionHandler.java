package com.directory.contact.exception;

import com.directory.contact.dto.ApiResponse;
import com.directory.contact.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice // This tells Spring: "I am watching all controllers for errors"
public class GlobalExceptionHandler {

        // 1. Catch our "Email already in use" RuntimeException
        @ExceptionHandler(ContactAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<Void>> handleContactAlreadyExistsException(ContactAlreadyExistsException ex) {
                ApiResponse<Void> error = new ApiResponse<>(
                                false,
                                ex.getMessage(),
                                null,
                                LocalDateTime.now());
                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(ContactNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleContactNotFoundException(ContactNotFoundException ex) {
                ApiResponse<Void> error = new ApiResponse<>(
                                false,
                                ex.getMessage(),
                                null,
                                LocalDateTime.now());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // 2. Catch Validation Errors (@NotBlank, @Pattern, etc.)
        @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(
                        org.springframework.web.bind.MethodArgumentNotValidException ex) {

                // Sort by field name for deterministic ordering, then pick the first message
                String firstError = ex.getBindingResult().getFieldErrors().stream()
                                .sorted(java.util.Comparator.comparing(
                                                org.springframework.validation.FieldError::getField))
                                .map(org.springframework.validation.FieldError::getDefaultMessage)
                                .findFirst()
                                .orElse("Validation failed");

                ErrorResponse error = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                firstError,
                                LocalDateTime.now());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
}