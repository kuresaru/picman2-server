package top.scraft.picmanserver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RgwAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${picman.rgw-access-key}")
    private String rgwAccessKey;

    public RgwAuthenticationFilter() {
        super("/rgwauth");
    }

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if ("GET".equals(request.getMethod()) && rgwAccessKey.equals(request.getHeader("X-Rgw-Access-Key"))) {
            return new RgwAuthentication();
        }
        throw new BadCredentialsException("Invalid token");
    }

}
