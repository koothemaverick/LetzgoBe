package com.letzgo.LetzgoBe.domain.map.exception;

import com.letzgo.LetzgoBe.domain.map.controller.MapController;
import com.letzgo.LetzgoBe.domain.map.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = MapController.class)
public class ExceptionControllerAdvice {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity ExceptionHandle(RuntimeException e) {
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
    }
}
