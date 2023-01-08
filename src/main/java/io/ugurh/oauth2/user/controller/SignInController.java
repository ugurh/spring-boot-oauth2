package io.ugurh.oauth2.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.ugurh.oauth2.user.models.UserRole;
import io.ugurh.oauth2.user.models.User;
import io.ugurh.oauth2.user.repository.UserRepository;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/signin")
public class SignInController {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    public SignInController(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public User signin(@RequestParam String email, @RequestParam String password) {
        User user = new User(null, email, passwordEncoder.encode(password), UserRole.USER, 0D, null);
        return repository.save(user);
    }

    @PostMapping("/validateEmail")
    public Boolean emailExists(@RequestParam String email) {
        return repository.existsByEmail(email);
    }

}