package car.demo.global.exception;

import car.demo.global.dto.CommonResponse;
import car.demo.global.exception.custom.AuthenticationException;
import car.demo.global.exception.custom.AuthorizationException;
import car.demo.global.exception.custom.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<CommonResponse<?>> handleBusinessException(BusinessException e) {
        log.error("handleBusinessException", e);
        return new ResponseEntity<>(CommonResponse.error(e.getMessage()), resolveStatus(e.getErrorCode()));
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<CommonResponse<?>> handleAuthenticationException(AuthenticationException e) {
        log.error("handleAuthenticationException", e);
        return new ResponseEntity<>(CommonResponse.error(e.getMessage()), resolveStatus(e.getErrorCode()));
    }

    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<CommonResponse<?>> handleAuthorizationException(AuthorizationException e) {
        log.error("handleAuthorizationException", e);
        return new ResponseEntity<>(CommonResponse.error(e.getMessage()), resolveStatus(e.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<CommonResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        return new ResponseEntity<>(CommonResponse.error(extractValidationMessage(e)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<CommonResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("handleConstraintViolationException", e);
        return new ResponseEntity<>(CommonResponse.error(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<CommonResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadableException", e);
        return new ResponseEntity<>(CommonResponse.error(ErrorCode.INVALID_REQUEST.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<CommonResponse<?>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("NoResourceFoundException", e);
        return new ResponseEntity<>(CommonResponse.error(ErrorCode.NOT_FOUND_PATH.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonResponse<?>> handleException(Exception e) {
        log.error("handleException", e);
        return new ResponseEntity<>(CommonResponse.error(ErrorCode.SERVER_ERROR.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String extractValidationMessage(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        return fieldError == null ? ErrorCode.INVALID_REQUEST.getMessage() : fieldError.getDefaultMessage();
    }

    private HttpStatus resolveStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case JWT_TOKEN_INVALID, JWT_TOKEN_EXPIRED, JWT_TOKEN_MISSING,
                 INVALID_PROVIDER_TOKEN_FORMAT, AUTHENTICATION_REQUIRED -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            case NOT_FOUND_ENTITY, NOT_FOUND_PATH -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
