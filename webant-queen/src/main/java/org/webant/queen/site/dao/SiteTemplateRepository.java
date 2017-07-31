package org.webant.queen.site.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.webant.queen.site.entity.SiteTemplate;

import java.util.List;

@Repository
public interface SiteTemplateRepository extends JpaRepository<SiteTemplate, Integer> {
    List<SiteTemplate> findAllByFingerPrint(String fingerPrint);
}
