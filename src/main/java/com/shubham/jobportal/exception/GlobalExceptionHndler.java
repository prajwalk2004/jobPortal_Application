package com.shubham.jobportal.exception;

import com.shubham.jobportal.dto.ErrorResposnseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
/*
when ever ther is any error occure inside controler layer /controller class then
it execute the code written inside this class
 */
public class  GlobalExceptionHndler {
    @ExceptionHandler(Exception.class)

    public ResponseEntity<ErrorResposnseDto> handleException(Exception exception , WebRequest webRequest){

        ErrorResposnseDto errorResposnseDto =new ErrorResposnseDto(webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResposnseDto,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /*used in the case of Object /Dto validation*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handelException(MethodArgumentNotValidException exception){
        Map<String,String> errors=new HashMap<>();
        List<FieldError>fieldErrors=exception.getBindingResult().getFieldErrors();
        fieldErrors.forEach(error->errors.put(error.getField(),error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
    // used in parameter validation
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String,String>> handlerMapper(HandlerMethodValidationException exception){
      Map<String,String> errors=new HashMap<>();
      List<ParameterValidationResult> results=exception.getParameterValidationResults();
      results.forEach( result->{
          String paramName=result.getMethodParameter().getParameterName();
          String combinedMessage=result.getResolvableErrors()
                  .stream()
                  .map(error->error.getDefaultMessage())
                  .collect(Collectors.joining(","));
          errors.put(paramName,combinedMessage);

      });
      return ResponseEntity.badRequest().body(errors);

    }
}
