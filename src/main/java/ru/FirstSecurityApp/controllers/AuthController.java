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
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.services.RegistrationService;
import ru.FirstSecurityApp.util.PersonValidator;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

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


    @PostMapping("/registration")
    public Map<String, String> performRegistration(@RequestBody @Valid Person person,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Если есть ошибки валидации, возвращаем сообщение об ошибке
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return Map.of("error", errorMessage);
        }
        // Регистрация пользователя
        registrationService.register(person);

        // Генерация JWT токена
        String token = jwtUtil.generateToken(person.getUsername());

        // Возвращение JWT токена в формате Map
        return Map.of("jwt-token", token);
    }


    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(), authenticationDTO.getPassword());
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            return Map.of("exception message", "Incorrect cridentials");
        }
        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return Map.of("jwt-token", token);
    }


    public Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }
}
