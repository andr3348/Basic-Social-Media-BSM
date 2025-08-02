package com.basicsocialmedia.backend.controller;

import com.basicsocialmedia.backend.model.User;
import com.basicsocialmedia.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    // GET /api/users(?username="")(?email="")
    @GetMapping
    public ResponseEntity<?> getUsersByQueryParam(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        if (username != null) {
            Optional<User> user = userService.getUserByUsername(username);
            return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        if (email != null) {
            Optional<User> user = userService.getUserByEmail(email);
            return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        List<User> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // PATCH /api/users/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUser(@PathVariable Integer id, User user) {
        User patchedUser = userService.patchUser(id, user);
        return new ResponseEntity<>(patchedUser, HttpStatus.OK);
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
