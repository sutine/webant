package org.webant.queen.link.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.webant.queen.link.entity.Link;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<Link, String> {
    Page<Link> findAllByStatus(String status, Pageable pageable);
    long countBySiteId(String siteId);

    @Query("SELECT l.status as status, count(l.status) as count FROM Link as l where siteId = ?1 group by l.status")
    List<ProgressSummary> groupByStatus(String siteId);

    @Query("SELECT link from Link link JOIN Site site " +
            "ON link.siteId = site.id " +
            "WHERE link.status = ?1 AND site.status = ?2")
    Page<Link> selectLinks(String linkStatus, String siteStatus, Pageable pageable);

    @Query("SELECT link from Link link, Site site, Node node " +
            "WHERE link.siteId = site.id AND link.nodeId = node.id " +
            "AND link.status = ?1 AND site.status = ?2 AND node.status = ?3")
    Page<Link> selectLinksWithNode(String linkStatus, String siteStatus, String nodeStatus, Pageable pageable);
}
