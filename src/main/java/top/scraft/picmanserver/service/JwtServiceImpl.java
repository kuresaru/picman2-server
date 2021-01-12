package top.scraft.picmanserver.service;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${picman.jwt.sign-key}")
    private String signKey;

    public String create(String subject, String payload, long expireSecond) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + (expireSecond * 1000));
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuer("picman2")
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expire)
                .setPayload(payload)
                .signWith(SignatureAlgorithm.HS512, signKey)
                .compact();
    }

    public Claims check(String jwt) {
        try {
            return Jwts.parser().setSigningKey(signKey).parseClaimsJws(jwt).getBody();
        } catch (Exception ignore) {
            return null;
        }
    }

}
