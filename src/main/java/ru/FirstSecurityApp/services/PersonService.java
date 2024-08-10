package ru.FirstSecurityApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.FirstSecurityApp.dtos.PersonDTO;
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.repositories.PeopleRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PeopleRepository peopleRepository;

    @Transactional(readOnly = true)
    public List<PersonDTO> getAllUsers() {
        List<Person> users = peopleRepository.findAll();
        return users.stream()
                .map(this::convertToPersonDto)
                .collect(Collectors.toList());
    }
    public PersonDTO getUserByEmail(String email) {
        return peopleRepository.findByEmail(email).map(this::convertToPersonDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private PersonDTO convertToPersonDto(Person person) {
        return PersonDTO.builder().
        username(person.getUsername()).
                yearOfBirth(person.getYearOfBirth()).
                role(person.getRole()).
                email(person.getEmail()).
                build();
    }
}
