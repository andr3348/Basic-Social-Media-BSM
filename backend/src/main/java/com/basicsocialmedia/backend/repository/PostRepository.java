package com.basicsocialmedia.backend.repository;

import com.basicsocialmedia.backend.model.Post;
import com.basicsocialmedia.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    // for the use to see their own (PUBLIC & PRIVATE posts by a specific user)
    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // only PUBLIC posts by a specific user (for user profile)
    Page<Post> findByUserAndIsPublicTrueOrderByCreatedAtDesc(User user, Pageable pageable);

    // find ALL public posts from all the existing users (for the home feed)
    Page<Post> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    Optional<Post> findByIdAndIsPublicTrue(Integer id);
}
