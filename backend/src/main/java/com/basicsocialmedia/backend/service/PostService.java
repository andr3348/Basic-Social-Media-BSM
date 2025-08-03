package com.basicsocialmedia.backend.service;

import com.basicsocialmedia.backend.model.Post;
import com.basicsocialmedia.backend.model.User;
import com.basicsocialmedia.backend.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) { this.postRepository = postRepository; }

    @Transactional
    public Post createPost(Post post, User user) {
        post.setUser(user);
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<Post> getAllPostsForUser(User user, Pageable pageable) {
        return postRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> getPublicPostsForUser(User user, Pageable pageable) {
        return postRepository.findByUserAndIsPublicTrueOrderByCreatedAtDesc(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> getPublicPosts(Pageable pageable) {
        return postRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Integer id) {
        return postRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPublicPostById(Integer id) {
        return postRepository.findByIdAndIsPublicTrue(id);
    }

    @Transactional
    public Post patchPost(Integer id, Post post) {
        Post patchedPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with the id: "+id));
        if (post.getContent() != null) {
            patchedPost.setContent(post.getContent());
        }
        if (post.getImageUrl() != null) {
            patchedPost.setImageUrl(post.getImageUrl());
        }
        if (post.getVideoUrl() != null) {
            patchedPost.setVideoUrl(post.getVideoUrl());
        }
        if (post.getIsPublic() != null) {
            patchedPost.setIsPublic(post.getIsPublic());
        }
        return postRepository.save(patchedPost);
    }

    @Transactional
    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }
}
