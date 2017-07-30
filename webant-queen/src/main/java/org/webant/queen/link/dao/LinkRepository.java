package org.webant.queen.link.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.webant.queen.link.entity.Link;

@Repository
public interface LinkRepository extends JpaRepository<Link, String> {
    Page<Link> findAllByStatus(String status, Pageable pageable);
}
