package com.communication.repository;

import com.communication.entity.ReadingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {

    Optional<ReadingHistory> findByUserIdAndContentId(Long userId, Long contentId);

    Page<ReadingHistory> findByUserIdOrderByReadAtDesc(Long userId, Pageable pageable);

    long countByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM ReadingHistory rh WHERE rh.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
