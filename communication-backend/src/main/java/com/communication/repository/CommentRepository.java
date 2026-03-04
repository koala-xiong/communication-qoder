package com.communication.repository;

import com.communication.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByContentIdAndParentIsNullOrderByCreatedAtDesc(Long contentId, Pageable pageable);

    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    long countByContentId(Long contentId);

    long countByUserId(Long userId);

    void deleteByContentId(Long contentId);

    boolean existsByIdAndUserId(Long id, Long userId);

    Page<Comment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.content.id = :contentId AND c.parent IS NULL")
    long countTopLevelByContentId(@Param("contentId") Long contentId);
}
