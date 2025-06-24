package com.rafaellor.forumhub.config;

import com.rafaellor.forumhub.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed for fields",
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Malformed JSON request body: " + Objects.requireNonNull(ex.getMessage()).split(";")[0],
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String detailMessage = Objects.requireNonNull(ex.getRootCause()).getMessage();
        String specificMessage = "Data integrity violation. This resource might already exist or has invalid related data.";
        if (detailMessage != null) {
            if (detailMessage.contains("Duplicate entry")) {
                specificMessage = "Duplicate entry detected: " + detailMessage.substring(detailMessage.indexOf("for key"));
            } else if (detailMessage.contains("Cannot add or update a child row")) {
                specificMessage = "Cannot add or update resource: foreign key constraint fails.";
            }
        }

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                specificMessage,
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Invalid username or password",
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "Access denied. You do not have permission to access this resource.",
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                null
        );
        ex.printStackTrace();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
