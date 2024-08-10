package ru.FirstSecurityApp.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.FirstSecurityApp.dtos.PersonDTO;
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.services.PersonService;
import ru.FirstSecurityApp.services.RegistrationService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonDTO>> getAllUsers() {
        List<PersonDTO> users = personService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<PersonDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(personService.getUserByEmail(email));
    }
}
