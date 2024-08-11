package ru.FirstSecurityApp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.FirstSecurityApp.dtos.PersonDTO;
import ru.FirstSecurityApp.services.PersonService;

import java.util.List;

/**
 * @swagger
 * @class PersonController
 * @description Контроллер для управления пользователями.
 *              Предоставляет методы для получения списка пользователей и информации о пользователе по email.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    /**
     * @method GET
     * @path /users
     * @description Получение списка всех пользователей.
     * @return Список DTO объектов пользователей.
     */
    @GetMapping
    public ResponseEntity<List<PersonDTO>> getAllUsers() {
        List<PersonDTO> users = personService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * @method GET
     * @path /users/{email}
     * @description Получение информации о пользователе по email.
     * @param email Email пользователя, информацию о котором нужно получить.
     * @return DTO объекта пользователя с указанным email.
     */
    @GetMapping("/{email}")
    public ResponseEntity<PersonDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(personService.getUserByEmail(email));
    }
}
