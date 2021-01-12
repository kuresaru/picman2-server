package top.scraft.picmanserver.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.scraft.picmanserver.data.SacUserPrincipal;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("@annotation(top.scraft.picmanserver.log.ApiLog)")
    public void apiLog() {
    }

    @Before("apiLog()")
    public void doBefore(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        String func = joinPoint.getSignature().getName();

        Principal principal = request.getUserPrincipal();
        String user = null;
        if (principal instanceof OAuth2Authentication) {
            if (((OAuth2Authentication) principal).isAuthenticated()) {
                Object p = ((OAuth2Authentication) principal).getUserAuthentication().getPrincipal();
                if (p instanceof SacUserPrincipal) {
                    user = ((SacUserPrincipal) p).getSaid() + "";
                }
            }
        }

        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();

        log.info("request - func={}, user={}, ip={}, method={}, url={}", func, user, ip, method, url);
    }

    @AfterReturning(returning = "response", pointcut = "apiLog()")
    public void doAfterReturning(Object response) throws Throwable {
    }

}
