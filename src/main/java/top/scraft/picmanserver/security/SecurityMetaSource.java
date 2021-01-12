package top.scraft.picmanserver.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于计算访问该资源需要的权限
 */
@Deprecated
//@Component
@Slf4j
public class SecurityMetaSource implements FilterInvocationSecurityMetadataSource {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private final Pattern libPattern = Pattern.compile("^/api/lib/([0-9]+).*$");

    /**
     * 计算访问该资源是否需要登录
     *
     * @param requestPath 访问路径
     * @return 需要登录返回true
     */
    private boolean isLoginRequired(String requestPath) {
        return !(antPathMatcher.match("/api/", requestPath));
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;
        HttpServletRequest request = fi.getRequest();
        Set<ConfigAttribute> attributes = new HashSet<>();
        String requestPath = urlPathHelper.getLookupPathForRequest(request);
        // 判断是否需要登录
        if (isLoginRequired(requestPath)) {
            attributes.add(new MyConfigAttribute("ATTR_LOGIN"));
        }
        // 访问图库判断是否有权访问
        if (antPathMatcher.match("/api/lib/**", requestPath)) {
            Matcher matcher = libPattern.matcher(requestPath);
            if (matcher.matches()) {

            }
        }
        return attributes;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

}
