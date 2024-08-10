package ru.FirstSecurityApp.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;


import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;


@Component
public class JwtUtil {

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String userName) {
        Date expiredDate = Date.from(ZonedDateTime.now().plusMinutes(jwtLifetime.toMinutes()).toInstant());
        return JWT.create()
                .withSubject("User details")
                .withClaim("username", userName)
                .withIssuedAt(new Date())
                .withIssuer("alishev")
                .withExpiresAt(expiredDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetriveClaim(String token) throws JWTVerificationException {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("alishev")
                .build();
        DecodedJWT jwt = jwtVerifier.verify(token);
        return jwt.getClaim("username").asString();
    }
}
