package com.communication.repository;

import com.communication.entity.ContentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentTagRepository extends JpaRepository<ContentTag, Long> {

    List<ContentTag> findByContentId(Long contentId);

    void deleteByContentId(Long contentId);

    @Query("SELECT DISTINCT ct.tag FROM ContentTag ct WHERE ct.tag LIKE %:keyword%")
    List<String> findTagsByKeyword(@Param("keyword") String keyword);

    @Query("SELECT ct.content.id FROM ContentTag ct WHERE ct.tag = :tag")
    List<Long> findContentIdsByTag(@Param("tag") String tag);

    @Query("SELECT ct.tag, COUNT(ct) as cnt FROM ContentTag ct GROUP BY ct.tag ORDER BY cnt DESC")
    List<Object[]> findPopularTags();

    boolean existsByContentIdAndTag(Long contentId, String tag);
}
