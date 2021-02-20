package top.scraft.picmanserver.data;

import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.Cookie;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginFinishSavedRequest implements SavedRequest {

    @Override
    public String getRedirectUrl() {
        return "/?login_finish";
    }

    @Override
    public List<Cookie> getCookies() {
        return null;
    }

    @Override
    public String getMethod() {
        return null;
    }

    @Override
    public List<String> getHeaderValues(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public List<Locale> getLocales() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

}
