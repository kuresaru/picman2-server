package top.scraft.picmanserver.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import top.scraft.picmanserver.data.SacUserPrincipal;
import top.scraft.picmanserver.service.UserService;

import javax.annotation.Resource;

@Component
public class AuthenticationSuccessEventHandler implements ApplicationListener<AuthenticationSuccessEvent> {

    @Resource
    private UserService userService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        if (authentication instanceof OAuth2Authentication) {
            Object principal = ((OAuth2Authentication) authentication).getUserAuthentication().getPrincipal();
            if (principal instanceof SacUserPrincipal) {
                userService.createUserIfNotExists((SacUserPrincipal) principal);
            }
        }
    }

}
