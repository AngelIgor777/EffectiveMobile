package ru.FirstSecurityApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.repositories.PeopleRepository;

/**
 * @author Neil Alishev
 */
@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(Person person) {
        if (peopleRepository.findByEmail(person.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        peopleRepository.save(person);
    }



}
