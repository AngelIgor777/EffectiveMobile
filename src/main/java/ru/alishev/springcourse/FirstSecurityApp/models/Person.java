package ru.alishev.springcourse.FirstSecurityApp.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов длиной")
    @Column(name = "username", nullable = false)
    private String username;

    @Min(value = 1900, message = "Год рождения должен быть больше, чем 1900")
    @Column(name = "year_of_birth", nullable = false)
    private int yearOfBirth;

    @NotEmpty(message = "Пароль не должен быть пустым")
    @Column(name = "password", nullable = false)
    private String password;

    @NotEmpty(message = "Роль не должна быть пустой")
    @Column(name = "role", nullable = false)
    private String role;

    @NotEmpty(message = "Email не должен быть пустым")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public Person(String username, int yearOfBirth, String password, String role, String email, List<Task> tasks) {
        this.username = username;
        this.yearOfBirth = yearOfBirth;
        this.password = password;
        this.role = role;
        this.email = email;
        this.tasks = tasks;
    }
}
