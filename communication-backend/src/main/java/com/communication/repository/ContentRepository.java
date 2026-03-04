package com.communication.repository;

import com.communication.entity.Content;
import com.communication.entity.ContentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    Page<Content> findByStatusOrderByCreatedAtDesc(ContentStatus status, Pageable pageable);

    Page<Content> findByAuthorIdAndStatusOrderByCreatedAtDesc(Long authorId, ContentStatus status, Pageable pageable);

    Page<Content> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.id = :id AND c.status = :status")
    Optional<Content> findByIdAndStatus(@Param("id") Long id, @Param("status") ContentStatus status);

    @Modifying
    @Query("UPDATE Content c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long id);

    List<Content> findByAuthorId(Long authorId);

    long countByAuthorId(Long authorId);

    long countByAuthorIdAndStatus(Long authorId, ContentStatus status);

    @Query("SELECT SUM(c.viewCount) FROM Content c WHERE c.author.id = :authorId")
    Long sumViewCountByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT SUM(c.commentCount) FROM Content c WHERE c.author.id = :authorId")
    Long sumCommentCountByAuthorId(@Param("authorId") Long authorId);

    Page<Content> findByAuthorIdInAndStatusOrderByCreatedAtDesc(List<Long> authorIds, ContentStatus status, Pageable pageable);

    // 搜索相关
    @Query("SELECT c FROM Content c WHERE c.status = :status AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.body) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.createdAt DESC")
    Page<Content> searchByKeyword(@Param("keyword") String keyword, @Param("status") ContentStatus status, Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.status = :status AND c.id IN :ids ORDER BY c.createdAt DESC")
    Page<Content> findByIdInAndStatus(@Param("ids") List<Long> ids, @Param("status") ContentStatus status, Pageable pageable);
}
