package ru.alishev.springcourse.FirstSecurityApp.dtos;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class PersonDTO {

        @NotEmpty(message = "Имя не должно быть пустым")
        @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов длиной")
        private String username;

        @Min(value = 1900, message = "Год рождения должен быть больше, чем 1900")
        private int yearOfBirth;

        private String password;

        @NotEmpty(message = "Роль не должна быть пустой")
        private String role;

        @NotEmpty(message = "Email не должен быть пустым")
        @Email(message = "Email должен быть корректным")
        private String email;

    }
