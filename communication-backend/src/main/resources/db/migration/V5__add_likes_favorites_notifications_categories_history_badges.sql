-- V5: Add likes, favorites, notifications, categories, reading_history, user_badges tables

-- 内容分类表
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    icon VARCHAR(50),
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_categories_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 为内容表添加分类外键和点赞计数
ALTER TABLE contents ADD COLUMN category_id BIGINT DEFAULT NULL AFTER status;
ALTER TABLE contents ADD COLUMN like_count INT DEFAULT 0 AFTER comment_count;
ALTER TABLE contents ADD CONSTRAINT fk_contents_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;
CREATE INDEX idx_contents_category ON contents(category_id);

-- 点赞表
CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES contents(id) ON DELETE CASCADE,
    UNIQUE KEY uk_like (user_id, content_id),
    INDEX idx_likes_user (user_id),
    INDEX idx_likes_content (content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 收藏表
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES contents(id) ON DELETE CASCADE,
    UNIQUE KEY uk_favorite (user_id, content_id),
    INDEX idx_favorites_user (user_id),
    INDEX idx_favorites_content (content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 通知表
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    type ENUM('LIKE', 'COMMENT', 'FOLLOW', 'SYSTEM') NOT NULL,
    content_id BIGINT DEFAULT NULL,
    comment_id BIGINT DEFAULT NULL,
    message VARCHAR(500),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES contents(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_notifications_recipient (recipient_id),
    INDEX idx_notifications_read (recipient_id, is_read),
    INDEX idx_notifications_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 阅读历史表
CREATE TABLE reading_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    read_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES contents(id) ON DELETE CASCADE,
    UNIQUE KEY uk_reading (user_id, content_id),
    INDEX idx_reading_user (user_id),
    INDEX idx_reading_time (user_id, read_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户徽章表
CREATE TABLE user_badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    badge_type ENUM('NEWCOMER', 'ACTIVE_WRITER', 'POPULAR_AUTHOR', 'TOP_COMMENTER', 'LOYAL_READER', 'EARLY_ADOPTER') NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_badge (user_id, badge_type),
    INDEX idx_badges_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 预置分类数据
INSERT INTO categories (name, description, icon, sort_order) VALUES
('技术', '编程、架构、DevOps 等技术话题', 'Monitor', 1),
('产品', '产品设计、用户体验、需求分析', 'Opportunity', 2),
('设计', '视觉设计、交互设计、品牌设计', 'Brush', 3),
('商业', '创业、投资、市场营销', 'TrendCharts', 4),
('科学', '前沿科技、学术研究、科普', 'MagicStick', 5),
('生活', '生活方式、旅行、美食', 'Coffee', 6),
('职场', '职业发展、求职面试、团队管理', 'Suitcase', 7),
('阅读', '书评、读书笔记、知识管理', 'Reading', 8);
