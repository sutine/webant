package org.webant.queen.site.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.webant.queen.site.dao.SiteTemplateRepository;
import org.webant.queen.site.entity.SiteTemplate;

import java.util.List;

@Service
public class SiteTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(SiteTemplateService.class);

    @Autowired
    private SiteTemplateRepository repository;

    public SiteTemplate get(String id) {
        return repository.findById(id).get();
    }

    public int save(List<SiteTemplate> list) {
        if (list == null || list.isEmpty())
            return 0;

        Iterable<SiteTemplate> affectRows = repository.saveAll(list);
        return list.size();
    }

    public String save(SiteTemplate entity) {
        if (entity == null && StringUtils.isEmpty(entity.getConfig()))
            return "";

        List<SiteTemplate> list = repository.findAllByFingerPrint(entity.getFingerPrint());
        if (!list.isEmpty())
            return list.get(0).getId();

        SiteTemplate save = repository.save(entity);
        return save.getId();
    }
}
