package car.demo.global.aspect;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class ApiLoggingAspect {

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerPointcut() {}

  @Around("controllerPointcut()")
  public Object logApiRequest(ProceedingJoinPoint joinPoint) throws Throwable {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    String method = request.getMethod();
    String uri = request.getRequestURI();
    String params = getParams(request);
    String controllerName = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().getName();

    log.info("[API Request] {} {} | Controller: {}.{} | Params: {}",
        method, uri, controllerName, methodName, params);

    long start = System.currentTimeMillis();
    try {
      Object result = joinPoint.proceed();
      long executionTime = System.currentTimeMillis() - start;
      log.info("[API Response] {} {} | Time: {}ms", method, uri, executionTime);
      return result;
    } catch (Exception e) {
      long executionTime = System.currentTimeMillis() - start;
      log.error("[API Error] {} {} | Time: {}ms | Error: {}", method, uri, executionTime, e.getMessage());
      throw e;
    }
  }

  private String getParams(HttpServletRequest request) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    if (parameterMap.isEmpty()) {
      return "none";
    }
    return parameterMap.entrySet().stream()
        .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
        .collect(Collectors.joining(", ", "[", "]"));
  }
}
