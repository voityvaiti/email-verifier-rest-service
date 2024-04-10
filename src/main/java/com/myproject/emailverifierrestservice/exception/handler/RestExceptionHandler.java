package com.myproject.emailverifierrestservice.exception.handler;

import com.myproject.emailverifierrestservice.dto.ErrorDetailsDto;
import com.myproject.emailverifierrestservice.exception.InvalidPasswordResetTokenException;
import com.myproject.emailverifierrestservice.exception.ResourceNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDetailsDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        String message = ex.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(" "));

        return new ResponseEntity<>(new ErrorDetailsDto(LocalDateTime.now(), message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorDetailsDto> handleResourceNotFound(ResourceNotFoundException ex) {

        return new ResponseEntity<>(new ErrorDetailsDto(LocalDateTime.now(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidPasswordResetTokenException.class})
    public ResponseEntity<ErrorDetailsDto> handleInvalidPasswordResetToken() {

        return new ResponseEntity<>(new ErrorDetailsDto(LocalDateTime.now(), "Password reset token is invalid."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetailsDto> handleOtherExceptions() {

        return new ResponseEntity<>(new ErrorDetailsDto(LocalDateTime.now(), "Something went wrong."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
