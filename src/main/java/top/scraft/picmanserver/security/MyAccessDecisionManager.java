package top.scraft.picmanserver.security;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于判断登录用户权限是否满足访问资源需要的权限
 */
@Deprecated
//@Component
public class MyAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        Set<String> required = new HashSet<>();
        configAttributes.forEach(c -> required.add(c.getAttribute()));
        if (authentication.isAuthenticated() && (!"anonymousUser".equals(authentication.getPrincipal()))) {
            required.remove("ATTR_LOGIN");
        }
        authentication.getAuthorities().forEach(a -> {
            String auth = a.getAuthority();
            if (auth.startsWith("SAID_")) {
                required.remove(auth);
            }
        });
        if (required.size() > 0) {
            // 有不满足的条件
            throw new AccessDeniedException("无权访问");
        }
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
