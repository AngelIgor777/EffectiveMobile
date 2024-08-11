package ru.FirstSecurityApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import ru.FirstSecurityApp.services.PersonDetailsService;

/**
 * Конфигурация безопасности приложения.
 * Этот класс настраивает защиту HTTP запросов, аутентификацию и шифрование паролей.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Сервис для работы с пользователями
    private final PersonDetailsService personDetailsService;

    // Фильтр для обработки JWT токенов
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService, JwtFilter jwtFilter) {
        this.personDetailsService = personDetailsService;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Настройка HTTP безопасности.
     * @param http объект конфигурации HTTP безопасности
     * @throws Exception если возникла ошибка при настройке
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // Отключаем CSRF защиту (не требуется для API)
                .authorizeRequests()
                .antMatchers("/auth/login", "/auth/registration", "/error", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Разрешаем доступ к страницам аутентификации и документации Swagger
                .anyRequest().hasAnyRole("USER", "ADMIN") // Все остальные запросы требуют авторизации
                .and()
                .formLogin()
                .loginPage("/auth/login") // Страница логина
                .loginProcessingUrl("/process_login") // URL для обработки формы логина
                .failureUrl("/auth/login?error") // URL для перенаправления при ошибке логина
                .and()
                .logout()
                .logoutUrl("/logout") // URL для выхода
                .logoutSuccessUrl("/auth/login") // URL для перенаправления после выхода
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Настройка для работы без сессий (используем JWT)

        // Добавляем фильтр для обработки JWT перед стандартным фильтром аутентификации
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Бин для настройки фаервола HTTP запросов.
     * @return объект фаервола
     */
    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

    /**
     * Настройка аутентификации пользователей.
     * @param auth объект конфигурации аутентификации
     * @throws Exception если возникла ошибка при настройке
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService) // Указываем сервис для получения данных о пользователях
                .passwordEncoder(passwordEncoder()); // Указываем способ шифрования паролей
    }

    /**
     * Бин для шифрования паролей с использованием BCrypt.
     * @return объект PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Используем BCrypt для шифрования паролей
    }

    /**
     * Бин для AuthenticationManager.
     * @return объект AuthenticationManager
     * @throws Exception если возникла ошибка при создании
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean(); // Возвращаем стандартный AuthenticationManager
    }
}
