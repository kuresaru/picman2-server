package top.scraft.picmanserver.config;

import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = SessionConfig.COOKIE_AGE, redisNamespace = "picman:session")
public class SessionConfig implements DefaultCookieSerializerCustomizer {

    protected static final int COOKIE_AGE = 86400 * 30;

    @Override
    public void customize(DefaultCookieSerializer serializer) {
        serializer.setUseBase64Encoding(false);
        serializer.setCookieMaxAge(COOKIE_AGE);
    }

}
