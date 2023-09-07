package com.bundleebackend.bundleebackend.util;

import com.bundleebackend.bundleebackend.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private static final long expirationDuration = 1000 * 60 * 60;
    @Value("${jwt.token.secret}")
    private String secretKey;

    public String generateToken(User user) {
        System.out.println(user);
        System.out.println(secretKey);
        String token =  Jwts.builder()
                .setSubject(user.getId() + "," + user.getUsername())
                .setIssuer("bundlee-backend")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationDuration))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("Token parsing error.");
            return false;
        }
    }
    public String getSubject(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public void test() {
        System.out.println("good");
    }
}
