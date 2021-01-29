package top.scraft.picmanserver.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import top.scraft.picmanserver.data.SacUserPrincipal;

import java.util.Collection;

public class RgwAuthentication implements Authentication {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public SacUserPrincipal getPrincipal() {
        SacUserPrincipal p = new SacUserPrincipal();
        p.setSaid(-1);
        return p;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return "RgwAuthentication";
    }

}
