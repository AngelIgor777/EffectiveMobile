package ru.alishev.springcourse.FirstSecurityApp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.alishev.springcourse.FirstSecurityApp.security.PersonDetails;


@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);



    @GetMapping("/showUserInfo")
    @ResponseBody
    public String showUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof PersonDetails)) {
            return "User not authenticated";
        }
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        logger.info("User details: {}", personDetails.getPerson());

        return "Username: " + personDetails.getUsername();
    }

}
