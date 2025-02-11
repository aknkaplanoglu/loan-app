package com.akotako.loanservice.aop;

import com.akotako.loanservice.model.exception.LoanAppException;
import com.akotako.loanservice.model.exception.UserNotFoundException;
import com.akotako.loanservice.model.response.ResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(LoanAppException.class)
    public ResponseEntity<ResponseObject<Void>> handleLoanAppException(LoanAppException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ResponseObject.failure(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(ResponseObject.failure(errors.toString()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseObject<Void>> handleAuthException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.failure("An unexpected error occurred: " + ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseObject<Void>> handleAuthExceptionGeneral(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.failure(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<Void>> handleGeneralException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(ResponseObject.failure("An unexpected error occurred: " + ex.getMessage()));
    }
}
