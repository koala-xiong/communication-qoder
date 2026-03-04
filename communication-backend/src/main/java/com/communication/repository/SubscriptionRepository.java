package com.communication.repository;

import com.communication.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsBySubscriberIdAndAuthorId(Long subscriberId, Long authorId);

    Optional<Subscription> findBySubscriberIdAndAuthorId(Long subscriberId, Long authorId);

    Page<Subscription> findBySubscriberIdOrderByCreatedAtDesc(Long subscriberId, Pageable pageable);

    Page<Subscription> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    long countBySubscriberId(Long subscriberId);

    long countByAuthorId(Long authorId);

    @Query("SELECT s.author.id FROM Subscription s WHERE s.subscriber.id = :subscriberId")
    List<Long> findAuthorIdsBySubscriberId(@Param("subscriberId") Long subscriberId);

    void deleteBySubscriberIdAndAuthorId(Long subscriberId, Long authorId);
}
