package top.scraft.picmanserver.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import top.scraft.picmanserver.data.SacUserPrincipal;
import top.scraft.picmanserver.rest.result.RootResult;
import top.scraft.picmanserver.service.UserService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//@Component
@Deprecated
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private UserService userService;
    @Resource
    private ObjectMapper objectMapper;

    private String successMessage;

    @PostConstruct
    public void init() throws JsonProcessingException {
        successMessage = objectMapper.writeValueAsString(new RootResult().ok());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2Authentication) {
            Object principal = ((OAuth2Authentication) authentication).getUserAuthentication().getPrincipal();
            if (principal instanceof SacUserPrincipal) {
                userService.createUserIfNotExists((SacUserPrincipal) principal);
            }
        }
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print(successMessage);
        out.flush();
        out.close();
    }

}
