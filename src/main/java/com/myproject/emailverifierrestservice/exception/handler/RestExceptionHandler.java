package com.myproject.emailverifierrestservice.exception.handler;

import com.myproject.emailverifierrestservice.dto.ErrorDetailsDto;
import com.myproject.emailverifierrestservice.exception.InvalidVerificationTokenException;
import com.myproject.emailverifierrestservice.exception.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
public class RestExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDetailsDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        log.warn("MethodArgumentNotValidException caught: {}", ex.getMessage());

        String message = ex.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(" "));

        return new ResponseEntity<>(new ErrorDetailsDto(LocalDateTime.now(), message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorDetailsDto> handleResourceNotFound(ResourceNotFoundException ex) {

        log.warn("ResourceNotFoundException caught: {}", ex.getMessage());

        return new ResponseEntity<>(new ErrorDetailsDto(LocalDateTime.now(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidVerificationTokenException.class})
    public ResponseEntity<ErrorDetailsDto> handleInvalidVerificationToken(InvalidVerificationTokenException e) {

        log.warn("InvalidVerificationTokenException caught");

        return new ResponseEntity<>(new ErrorDetailsDto(LocalDateTime.now(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
