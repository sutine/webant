package org.webant.queen.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webant.queen.data.dao.DataRepository;
import org.webant.queen.data.entity.Data;

import java.util.List;

@Service
public class DataService {
    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private DataRepository repository;

    public Page<Data> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Data get(String id) {
        return repository.findById(id).get();
    }

    public int save(List<Data> list) {
        if (list == null || list.isEmpty())
            return 0;

        Iterable<Data> affectRows = repository.saveAll(list);
        return list.size();
    }

    public int save(Data entity) {
        if (entity == null)
            return 0;
        repository.save(entity);
        return 1;
    }
}
