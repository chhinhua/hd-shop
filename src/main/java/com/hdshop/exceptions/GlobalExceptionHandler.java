package com.hdshop.exceptions;

import com.hdshop.dtos.ErrorDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chhin Hua - 07-10-2023
 */

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Xử lý ngoại lệ ResourceNotFoundException và trả về phản hồi HTTP 404.
     * @param exception Ngoại lệ ResourceNotFoundException
     * @param webRequest WebRequest của yêu cầu
     * @return ResponseEntity chứa thông tin chi tiết về lỗi và mã trạng thái HTTP 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                        WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Xử lý ngoại lệ APIException và trả về phản hồi HTTP 400.
     * @param exception Ngoại lệ APIException
     * @param webRequest WebRequest của yêu cầu
     * @return ResponseEntity chứa thông tin chi tiết về lỗi và mã trạng thái HTTP 400
     */
    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorDetails> handleAPIException(APIException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý ngoại lệ Exception chung và trả về phản hồi HTTP 400.
     * @param exception   Ngoại lệ Exception chung
     * @param webRequest  WebRequest của yêu cầu
     * @return ResponseEntity chứa thông tin chi tiết về lỗi và mã trạng thái HTTP 400
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalExceptionHandler(Exception exception, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý ngoại lệ MethodArgumentNotValidException và trả về phản hồi HTTP phù hợp.
     * @param exception Ngoại lệ MethodArgumentNotValidException
     * @param headers HttpHeaders của yêu cầu
     * @param status HttpStatusCode của phản hồi
     * @param request WebRequest của yêu cầu
     * @return ResponseEntity chứa danh sách các lỗi và mã trạng thái HTTP 400
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        if (exception instanceof BindException) {
            return handleBindException((BindException)exception);
        }

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    /**
     * Xử lý ngoại lệ BindException và trả về phản hồi HTTP phù hợp.
     * @param exception
     * @return ResponseEntity chứa danh sách các lỗi và mã trạng thái HTTP 400
     */
    public ResponseEntity<Object> handleBindException(BindException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
