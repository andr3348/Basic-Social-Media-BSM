package com.basicsocialmedia.backend.controller;

import com.basicsocialmedia.backend.model.Post;
import com.basicsocialmedia.backend.model.User;
import com.basicsocialmedia.backend.service.PostService;
import com.basicsocialmedia.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    // POST /api/posts
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userService.getUserByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Post createdPost = postService.createPost(post, user);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    // GET /api/posts/my_posts(?page=1&size=20)
    @GetMapping("/my_posts") // PUBLIC & PRIVATE posts for current user
    public ResponseEntity<Page<Post>> getMyPosts(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userService.getUserByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        Page<Post> myPosts = postService.getAllPostsForUser(user, pageable);
        return new ResponseEntity<>(myPosts, HttpStatus.OK);
    }

    // GET /api/posts/public(?username=""&page=1&size=20)
    @GetMapping("/public") // for home feed, or public user profile posts
    public ResponseEntity<Page<Post>> getPublicPosts(
            @RequestParam(required = false) String username,
            Pageable pageable) {
        Page<Post> posts;

        if (username != null && !username.isBlank()) {
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            posts = postService.getPublicPostsForUser(user, pageable);
        } else {
            posts = postService.getPublicPosts(pageable);
        }

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /*
    // GET /api/posts(?page=1&size=20)
    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(Pageable pageable) {
        Page<Post> allPosts = postService.getAllPosts(pageable);
        return new ResponseEntity<>(allPosts, HttpStatus.OK);
    }
    */

    /*
    // GET /api/posts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@RequestParam Integer id) {
        return postService.getPostById(id)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    */

    // GET /api/posts/{id}
    @GetMapping("/{id}") // public post only
    public ResponseEntity<Post> getPostById(@PathVariable Integer id) {
        return postService.getPublicPostById(id)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // PATCH /api/posts/{id}
    @PatchMapping("/{id}") // user can edit their own post whether it's public or private
    public ResponseEntity<Post> patchPost(@PathVariable Integer id, @RequestBody Post post) {
        Post foundPost = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with the id: "+id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userService.getUserByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));

        if (!user.getId().equals(foundPost.getUser().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return a 403 Forbidden
        }

        Post patchedPost = postService.patchPost(id, post);
        return new ResponseEntity<>(patchedPost, HttpStatus.OK);
    }
}
