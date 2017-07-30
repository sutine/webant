package org.webant.queen.node.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webant.queen.node.dao.NodeRepository;
import org.webant.queen.node.entity.Node;

import java.util.List;

@Service
public class NodeService {
    private static final Logger logger = LoggerFactory.getLogger(NodeService.class);

    @Autowired
    private NodeRepository repository;

    public Page<Node> list(String status, Pageable pageable) {
        return repository.findAllByStatus(status, pageable);
    }

    public Node get(String id) {
        return repository.findById(id).get();
    }

    public int save(List<Node> list) {
        if (list == null || list.isEmpty())
            return 0;

        Iterable<Node> affectRows = repository.saveAll(list);
        return list.size();
    }

    public int save(Node entity) {
        if (entity == null)
            return 0;
        repository.save(entity);
        return 1;
    }
}
