package com.communication.repository;

import com.communication.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndContentId(Long userId, Long contentId);

    Optional<Favorite> findByUserIdAndContentId(Long userId, Long contentId);

    long countByContentId(Long contentId);

    long countByUserId(Long userId);

    @Query("SELECT f.content.id FROM Favorite f WHERE f.user.id = :userId AND f.content.id IN :contentIds")
    List<Long> findFavoritedContentIds(@Param("userId") Long userId, @Param("contentIds") List<Long> contentIds);

    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
