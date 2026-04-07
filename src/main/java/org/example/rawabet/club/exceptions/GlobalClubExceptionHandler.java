package org.example.rawabet.club.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class GlobalClubExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)

  public Map<String,String> handleNotFound(NotFoundException ex){

    Map<String,String> error = new HashMap<>();

    error.put("error",ex.getMessage());

    return error;

  }

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)

  public Map<String,String> handleBusiness(BusinessException ex){

    Map<String,String> error = new HashMap<>();

    error.put("error",ex.getMessage());

    return error;

  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)

  public Map<String,String> handleValidation(
          MethodArgumentNotValidException ex){

    Map<String,String> error = new HashMap<>();

    error.put("error",
            ex.getBindingResult()
                    .getFieldError()
                    .getDefaultMessage());

    return error;

  }

}