package com.basicsocialmedia.backend.controller;

import com.basicsocialmedia.backend.model.User;
import com.basicsocialmedia.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    // GET /api/users(?username="")(?email="")&page=1&size=20
    @GetMapping
    public ResponseEntity<?> getUsersByQueryParam(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email, Pageable pageable) {
        if (username != null && !username.isBlank() && (email == null || email.isBlank())) {
            Optional<User> user = userService.getUserByUsername(username);
            return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        if (email != null && !email.isBlank() && (username == null || username.isBlank())) {
            Optional<User> user = userService.getUserByEmail(email);
            return user.map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        Page<User> allUsers = userService.getAllUsers(pageable);
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
    @PatchMapping("/{id}") // user can patch their own info
    public ResponseEntity<User> patchUser(@PathVariable Integer id,@RequestBody User user) {
        User requestedUser = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with the id: "+id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userService.getUserByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!currentUser.getId().equals(requestedUser.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User patchedUser = userService.patchUser(id, user);
        return new ResponseEntity<>(patchedUser, HttpStatus.OK);
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}") // user can delete their own account
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        User requestedUser = userService.getUserById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with id: "+id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!currentUser.getId().equals(requestedUser.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
