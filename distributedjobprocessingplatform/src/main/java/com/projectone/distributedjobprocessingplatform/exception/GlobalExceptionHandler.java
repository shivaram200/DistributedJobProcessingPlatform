package com.projectone.distributedjobprocessingplatform.exception;


import com.projectone.distributedjobprocessingplatform.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJobNotFound(JobNotFoundException exception, HttpServletRequest request){



        ErrorResponse er = new ErrorResponse();
        er.setTime(LocalDateTime.now());
        er.setStatus("404");
        er.setError("JOB_NOT_FOUND");
        er.setMessage(exception.getMessage());
        er.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(er);


    }


    @ExceptionHandler(InvalidJobException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJob(InvalidJobException exception,HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setStatus("400");
        errorResponse.setError("INVALID_JOB");
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(UnexpectedException exception,HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setStatus("500");
        errorResponse.setError("UNEXPECTED_ERROR");
        errorResponse.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();

        FieldError fieldError = exception.getBindingResult()
                .getFieldErrors()
                .get(0);

        String message = fieldError.getField() + " " + fieldError.getDefaultMessage();
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setStatus("400");
        errorResponse.setError("INVALID_JOB_BODY");
        errorResponse.setMessage(message);
        errorResponse.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


}
