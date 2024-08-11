package ru.FirstSecurityApp.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.FirstSecurityApp.services.PersonDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр для проверки и обработки JWT токенов.
 * Этот фильтр проверяет наличие и валидность JWT токена в запросе и устанавливает аутентификацию пользователя в контексте безопасности Spring.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    // Сервис для работы с JWT токенами
    private final JwtUtil jwtUtil;
    // Сервис для загрузки данных пользователя
    private final PersonDetailsService personDetailsService;

    /**
     * Конструктор для внедрения зависимостей.
     * @param jwtUtil сервис для работы с JWT
     * @param personDetailsService сервис для загрузки данных пользователя
     */
    @Autowired
    public JwtFilter(JwtUtil jwtUtil, PersonDetailsService personDetailsService) {
        this.jwtUtil = jwtUtil;
        this.personDetailsService = personDetailsService;
    }

    /**
     * Метод фильтрации, который вызывается для каждого HTTP запроса.
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException если произошла ошибка сервлета
     * @throws IOException если произошла ошибка ввода/вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Получаем заголовок Authorization из запроса
        String authorization = request.getHeader("Authorization");

        // Проверяем, что заголовок не пуст и начинается с "Bearer "
        if (authorization != null && !authorization.isBlank() && authorization.startsWith("Bearer ")) {
            // Извлекаем JWT токен из заголовка
            String jwt = authorization.substring(7);

            // Проверяем, что токен не пустой
            if (jwt.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT TOKEN IS BLANK OR INVALID");
                return;
            }

            try {
                // Проверяем токен и извлекаем имя пользователя
                String username = jwtUtil.validateTokenAndRetriveClaim(jwt);
                // Загружаем детали пользователя
                UserDetails userDetails = personDetailsService.loadUserByUsername(username);

                // Создаем объект аутентификации
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

                // Если в контексте безопасности нет аутентификации, устанавливаем её
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (JWTVerificationException e) {
                // Если токен невалидный, возвращаем ошибку
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT token");
                return;
            }
        }

        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}
