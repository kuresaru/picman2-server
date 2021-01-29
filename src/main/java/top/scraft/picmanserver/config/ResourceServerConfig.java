package top.scraft.picmanserver.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import top.scraft.picmanserver.data.SacUserPrincipal;
import top.scraft.picmanserver.security.MyAuthenticationEntryPoint;
import top.scraft.picmanserver.security.RgwAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Configuration
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private MyAuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    private RgwAuthenticationFilter rgwAuthenticationFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/").permitAll()
                .antMatchers("/rgwauth").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .addFilterAfter(rgwAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(myLoginSuccessRedirectSetterFilter(), AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/webjars/**", "/favicon.ico")
                .antMatchers(
                        "/v2/api-docs",//swagger api json
                        "/swagger-resources/configuration/ui",//用来获取支持的动作
                        "/swagger-resources",//用来获取api-docs的URI
                        "/swagger-resources/configuration/security",//安全选项
                        "/swagger-ui.html"
                );
    }

    @Bean
    public PrincipalExtractor principalExtractor() {
        return map -> {
            Map pm = (Map) map.get("principal");
            SacUserPrincipal principal = new SacUserPrincipal();
            principal.setSaid(Long.parseLong(pm.get("said").toString()));
            principal.setUsername((String) pm.get("username"));
            principal.setNickname((String) pm.get("nickname"));
            return principal;
        };
    }

    @Bean
    public Filter myLoginSuccessRedirectSetterFilter() {
        final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/login");
        final RequestCache requestCache = new HttpSessionRequestCache();
        return (request, response, chain) -> {
            HttpServletRequest r = (HttpServletRequest) request;
            if (requestMatcher.matches(r)) {
                requestCache.removeRequest((HttpServletRequest) request, null);
            }
            chain.doFilter(request, response);
        };
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
/*
org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@790bd0e,
org.springframework.security.web.context.SecurityContextPersistenceFilter@23471b2f,
org.springframework.security.web.header.HeaderWriterFilter@7bd4f212,
org.springframework.security.web.csrf.CsrfFilter@182ce25e,
org.springframework.security.web.authentication.logout.LogoutFilter@5e05dd42,
top.scraft.picmanserver.security.RgwAuthenticationFilter@5a85f41b,
top.scraft.picmanserver.config.ResourceServerConfig$$Lambda$813/0x00000008007b1040@17d837ab,
org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter@3cd6cab5,
org.springframework.security.web.savedrequest.RequestCacheAwareFilter@68893394,
org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@70bbc163,
org.springframework.security.web.authentication.AnonymousAuthenticationFilter@626a4e0a,
org.springframework.security.web.session.SessionManagementFilter@7da70124,
org.springframework.security.web.access.ExceptionTranslationFilter@3a4181ba,
org.springframework.security.web.access.intercept.FilterSecurityInterceptor@43ef1243
 */