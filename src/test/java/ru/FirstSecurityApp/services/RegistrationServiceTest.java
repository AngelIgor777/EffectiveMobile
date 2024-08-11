package ru.FirstSecurityApp.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.repositories.PeopleRepository;
import ru.FirstSecurityApp.services.RegistrationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private PeopleRepository peopleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister_Success() {
        Person person = Person.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .yearOfBirth(1990)
                .build();

        when(peopleRepository.findByEmail(person.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(person.getPassword())).thenReturn("encodedPassword");

        registrationService.register(person);

        verify(peopleRepository, times(1)).save(person);
        verify(passwordEncoder, times(1)).encode(person.getPassword());
        assertEquals("encodedPassword", person.getPassword());
        assertEquals("ROLE_USER", person.getRole());
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        Person person = Person.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .yearOfBirth(1990)
                .build();

        when(peopleRepository.findByEmail(person.getEmail())).thenReturn(Optional.of(person));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.register(person);
        });

        assertEquals("Пользователь с таким email уже существует", thrown.getMessage());
        verify(peopleRepository, times(0)).save(person);
    }
}
