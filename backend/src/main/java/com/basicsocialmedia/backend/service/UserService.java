package com.basicsocialmedia.backend.service;

import com.basicsocialmedia.backend.model.User;
import com.basicsocialmedia.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    @Transactional // <-- if there's an error it will be rolled back
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User patchUser(Integer id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with the id :"+id));

        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }

        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        if (user.getFullName() != null) {
            existingUser.setFullName(user.getFullName());
        }

        if (user.getBio() != null) {
            existingUser.setBio(user.getBio());
        }

        if (user.getProfilePictureUrl() != null) {
            existingUser.setProfilePictureUrl(user.getProfilePictureUrl());
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
