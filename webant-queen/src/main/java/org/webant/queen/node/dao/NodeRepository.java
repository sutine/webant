package org.webant.queen.node.dao;

import org.springframework.data.domain.Page;
        import org.springframework.data.domain.Pageable;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.stereotype.Repository;
        import org.webant.queen.node.entity.Node;

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {
    Page<Node> findAllByStatus(String status, Pageable pageable);
}
