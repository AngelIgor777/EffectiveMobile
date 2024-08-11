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

/**
 * Утилитный класс для работы с JWT (JSON Web Token).
 * Этот класс содержит методы для генерации и проверки JWT токенов.
 */
@Component
public class JwtUtil {

    // Длительность действия токена, считываемая из конфигурации
    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    // Секретный ключ для подписывания токенов, считываемый из конфигурации
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Генерирует JWT токен для указанного пользователя.
     * @param userName имя пользователя, для которого генерируется токен
     * @return сгенерированный JWT токен
     */
    public String generateToken(String userName) {
        // Вычисляем дату истечения действия токена
        Date expiredDate = Date.from(ZonedDateTime.now().plusMinutes(jwtLifetime.toMinutes()).toInstant());

        // Создаем JWT токен
        return JWT.create()
                // Устанавливаем субъект токена (например, "User details")
                .withSubject("User details")
                // Добавляем имя пользователя в полезную нагрузку токена
                .withClaim("username", userName)
                // Устанавливаем дату создания токена
                .withIssuedAt(new Date())
                // Устанавливаем издателя токена
                .withIssuer("igorAngelcev")
                // Устанавливаем дату истечения действия токена
                .withExpiresAt(expiredDate)
                // Подписываем токен с использованием секретного ключа
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * Проверяет JWT токен на валидность и извлекает имя пользователя.
     * @param token JWT токен
     * @return имя пользователя, извлеченное из токена
     * @throws JWTVerificationException если токен невалидный
     */
    public String validateTokenAndRetriveClaim(String token) throws JWTVerificationException {
        // Создаем объект для проверки токенов с использованием секретного ключа
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret))
                // Устанавливаем ожидаемый субъект токена
                .withSubject("User details")
                // Устанавливаем ожидаемого издателя токена
                .withIssuer("igorAngelcev")
                // Создаем объект проверки токена
                .build();

        // Проверяем токен и извлекаем его содержимое
        DecodedJWT jwt = jwtVerifier.verify(token);

        // Извлекаем имя пользователя из полезной нагрузки токена
        return jwt.getClaim("username").asString();
    }
}
