package car.demo.global.exception.custom;

import car.demo.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class JwtException extends RuntimeException {
    private final ErrorCode errorCode;

    public JwtException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
