package io.ugurh.oauth2.user.controller;

import java.util.HashSet;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.ugurh.oauth2.errors.EntityNotFoundException;
import io.ugurh.oauth2.user.models.UserRole;
import io.ugurh.oauth2.user.models.User;
import io.ugurh.oauth2.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@Validated
public class UserController {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;

	UserController(UserRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public Page<User> all(@PageableDefault(size = Integer.MAX_VALUE) Pageable pageable, OAuth2Authentication authentication) {
		String auth = (String) authentication.getUserAuthentication().getPrincipal();
		String role = authentication.getAuthorities().iterator().next().getAuthority();
		if (role.equals(UserRole.USER.name())) {
			return repository.findAllByEmail(auth, pageable);
		}
		return repository.findAll(pageable);
	}

	@GetMapping("/search")
	public Page<User> search(@RequestParam String email, Pageable pageable, OAuth2Authentication authentication) {
		String auth = (String) authentication.getUserAuthentication().getPrincipal();
		String role = authentication.getAuthorities().iterator().next().getAuthority();
		if (role.equals(UserRole.USER.name())) {
			return repository.findAllByEmailContainsAndEmail(email, auth, pageable);
		}
		return repository.findByEmailContains(email, pageable);
	}

	@GetMapping("/findByEmail")
	@PreAuthorize("!hasAuthority('USER') || (authentication.principal == #email)")
	public User findByEmail(@RequestParam String email, OAuth2Authentication authentication) {
		return repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(User.class, "email", email));
	}

	@GetMapping("/{id}")
	@PostAuthorize("!hasAuthority('USER') || (returnObject != null && returnObject.email == authentication.principal)")
	public User one(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(User.class, "id", id.toString()));
	}

	@PutMapping("/{id}")
	@PreAuthorize("!hasAuthority('USER') || (authentication.principal == @userRepository.findById(#id).orElse(new net.reliqs.gleeometer.users.User()).email)")
	public void update(@PathVariable Long id, @Valid @RequestBody User res) {
		User u = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(User.class, "id", id.toString()));
		res.setPassword(u.getPassword());
		res.setGlee(u.getGlee());
		repository.save(res);
	}

	@PostMapping
	@PreAuthorize("!hasAuthority('USER')")
	public User create(@Valid @RequestBody User res) {
		return repository.save(res);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("!hasAuthority('USER')")
	public void delete(@PathVariable Long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
		} else {
			throw new EntityNotFoundException(User.class, "id", id.toString());
		}
	}

	@PutMapping("/{id}/changePassword")
	@PreAuthorize("!hasAuthority('USER') || (#oldPassword != null && !#oldPassword.isEmpty() && authentication.principal == @userRepository.findById(#id).orElse(new net.reliqs.gleeometer.users.User()).email)")
	public void changePassword(@PathVariable Long id, @RequestParam(required = false) String oldPassword, @Valid @Size(min = 3) @RequestParam String newPassword) {
		User user = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(User.class, "id", id.toString()));
		if (oldPassword == null || oldPassword.isEmpty() || passwordEncoder.matches(oldPassword, user.getPassword())) {
			user.setPassword(passwordEncoder.encode(newPassword));
			repository.save(user);
		} else {
			throw new ConstraintViolationException("old password doesn't match", new HashSet<>());
		}
	}
	
}
