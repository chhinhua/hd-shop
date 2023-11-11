package com.hdshop.exception;

import com.hdshop.dto.ErrorDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
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
    @Autowired
    private MessageSource messageSource;

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

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<ErrorDetails> handleInvalidException(InvalidException exception,
                                                                        WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                messageSource.getMessage("authentication-error", null, LocaleContextHolder.getLocale()),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
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
     * Xử lý ngoại lệ khi quyền truy cập bị từ chối (Access Denied).
     *
     * @date 12-11-2023
     * @param webRequest   Đối tượng WebRequest chứa thông tin về yêu cầu web.
     * @return ResponseEntity chứa thông tin lỗi dưới dạng đối tượng ErrorDetails và mã HTTP 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                messageSource.getMessage("you-do-not-have-permission-to-perform-this-operation", null, LocaleContextHolder.getLocale()),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
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
