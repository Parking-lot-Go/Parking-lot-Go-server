package car.demo.global.exception;

import car.demo.global.dto.CommonResponse;
import car.demo.global.exception.custom.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<CommonResponse<?>> handleBusinessException(BusinessException e) {
        log.error("handleBusinessException", e);
        return new ResponseEntity<>(CommonResponse.error(e.getMessage()), HttpStatus.BAD_REQUEST);
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
}
