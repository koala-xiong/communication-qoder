package com.communication.repository;

import com.communication.entity.BadgeType;
import com.communication.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findByUserIdOrderByEarnedAtDesc(Long userId);

    boolean existsByUserIdAndBadgeType(Long userId, BadgeType badgeType);
}
