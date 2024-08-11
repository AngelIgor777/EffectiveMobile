package ru.FirstSecurityApp.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.FirstSecurityApp.config.JwtUtil;
import ru.FirstSecurityApp.dtos.AuthenticationDTO;
import ru.FirstSecurityApp.dtos.PersonDTO;
import ru.FirstSecurityApp.dtos.PersonRegister;
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.services.RegistrationService;
import ru.FirstSecurityApp.util.PersonValidator;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @swagger
 * @class AuthController
 * @description Контроллер для управления аутентификацией и регистрацией пользователей.
 *              Обрабатывает запросы на регистрацию новых пользователей и вход существующих.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final PersonValidator personValidator;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(RegistrationService registrationService, PersonValidator personValidator, JwtUtil jwtUtil, ModelMapper modelMapper, AuthenticationManager authenticationManager) {
        this.registrationService = registrationService;
        this.personValidator = personValidator;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
    }

    /**
     * @method POST
     * @path /auth/registration
     * @description Регистрация нового пользователя.
     *              Валидирует данные пользователя и сохраняет его в базу данных.
     * @param personRegister Объект с данными пользователя для регистрации.
     * @param bindingResult Объект для обработки ошибок валидации.
     * @return JWT токен или сообщение об ошибке валидации.
     */
    @PostMapping("/registration")
    public Map<String, String> performRegistration(@RequestBody @Valid PersonRegister personRegister,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Если есть ошибки валидации, возвращаем сообщение об ошибке
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return Map.of("error", errorMessage);
        }

        // Преобразоваем PersonRegister в Person
        Person person = Person.builder()
                .username(personRegister.getUsername())
                .email(personRegister.getEmail())
                .password(personRegister.getPassword()) // Храним пароль в зашифрованном виде
                .yearOfBirth(personRegister.getYearOfBirth())
                .role("USER") // Устанавливаем роль по умолчанию
                .build();

        // Регистрация пользователя
        registrationService.register(person);

        // Генерация JWT токена
        String token = jwtUtil.generateToken(person.getUsername());

        // Возвращение JWT токена в формате Map
        return Map.of("jwt-token", token);
    }

    /**

     * @method POST
     * @path /auth/login
     * @description Аутентификация пользователя.
     *              Проверяет учетные данные пользователя и возвращает JWT токен при успешной аутентификации.
     * @param authenticationDTO Объект с данными для аутентификации.
     * @return JWT токен или сообщение об ошибке аутентификации.
     */
    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(), authenticationDTO.getPassword());
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            return Map.of("exception message", "Incorrect credentials");
        }
        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return Map.of("jwt-token", token);
    }

    /**
     * @method convertToPerson
     * @description Преобразование DTO объекта PersonDTO в сущность Person.
     * @param personDTO Объект DTO для преобразования.
     * @return Объект сущности Person.
     */
    public Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }
}
