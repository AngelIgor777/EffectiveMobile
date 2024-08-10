package ru.alishev.springcourse.FirstSecurityApp.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alishev.springcourse.FirstSecurityApp.models.Person;
import ru.alishev.springcourse.FirstSecurityApp.services.RegistrationService;

@RestController
@RequestMapping("/users")
public class PersonController {

    @Autowired
    private RegistrationService personService;

    @PostMapping
    public ResponseEntity.BodyBuilder registerUser(@RequestBody Person person) {
        personService.register(person);
        return ResponseEntity.ok();
    }

    @GetMapping("/{email}")
    public ResponseEntity<Person> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(personService.getUserByEmail(email));
    }
}
