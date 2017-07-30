package org.webant.queen.site.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.webant.queen.site.entity.Site;

@Repository
public interface SiteRepository extends JpaRepository<Site, String> {
}
