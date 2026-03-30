package car.demo.global.security;

import car.demo.global.dto.CommonResponse;
import car.demo.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityResponseHandler {

    private final ObjectMapper objectMapper;

    public void handleSecurityError(HttpServletRequest request,
                                    HttpServletResponse response,
                                    ErrorCode errorCode,
                                    HttpStatus status,
                                    Exception e) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CommonResponse<Object> errorResponse = CommonResponse.error(errorCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
