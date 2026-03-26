package com.communication.repository;

import com.communication.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndContentId(Long userId, Long contentId);

    Optional<Like> findByUserIdAndContentId(Long userId, Long contentId);

    long countByContentId(Long contentId);

    @Query("SELECT l.content.id FROM Like l WHERE l.user.id = :userId AND l.content.id IN :contentIds")
    List<Long> findLikedContentIds(@Param("userId") Long userId, @Param("contentIds") List<Long> contentIds);

    Page<Like> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
