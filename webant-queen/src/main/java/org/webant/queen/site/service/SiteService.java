package org.webant.queen.site.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webant.queen.site.dao.SiteRepository;
import org.webant.queen.site.entity.Site;

import java.util.List;

@Service
public class SiteService {
    private static final Logger logger = LoggerFactory.getLogger(SiteService.class);

    @Autowired
    private SiteRepository repository;

    public Site get(String id) {
        return repository.findById(id).get();
    }

    public int save(List<Site> list) {
        if (list == null || list.isEmpty())
            return 0;

        Iterable<Site> affectRows = repository.saveAll(list);
        return list.size();
    }

    public int save(Site entity) {
        if (entity == null)
            return 0;
        repository.save(entity);
        return 1;
    }
}
