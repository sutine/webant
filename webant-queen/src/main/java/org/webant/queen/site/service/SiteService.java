package org.webant.queen.site.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.webant.queen.commons.entity.Progress;
import org.webant.queen.commons.exception.QueenException;
import org.webant.queen.link.entity.Link;
import org.webant.queen.link.service.LinkService;
import org.webant.queen.site.dao.SiteRepository;
import org.webant.queen.site.entity.Site;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SiteService {
    private static final Logger logger = LoggerFactory.getLogger(SiteService.class);

    @Autowired
    private SiteRepository repository;

    @Autowired
    private LinkService linkService;

    private ConcurrentMap<String, SiteManager> sites = new ConcurrentHashMap<>();

    public int save(Site entity) {
        if (entity == null)
            return 0;
        repository.save(entity);
        return 1;
    }

    public Site get(String id) {
        return repository.findById(id).get();
    }

    public int save(List<Site> list) {
        if (list == null || list.isEmpty())
            return 0;

        Iterable<Site> affectRows = repository.saveAll(list);
        return list.size();
    }

    public List<Link> genSeedLinks() {
        List<Link> links = new LinkedList<>();
        if (sites.isEmpty()) {
            List<Site> list = repository.findAll();
            for (Site site : list) {
                sites.put(site.getId(), new SiteManager(site));
            }
        }
        for (String key : sites.keySet()) {
            List<Link> list = sites.get(key).genSeedLinks();
            if (!list.isEmpty())
                links.addAll(list);
        }
        return links;
    }

    public void action(String siteId, String operation) throws QueenException {
        if (StringUtils.isEmpty(siteId))
            throw new QueenException("site id can not be empty!");

        if (StringUtils.isEmpty(operation))
            throw new QueenException("site id can not be empty!");

        Optional<Site> optional = repository.findById(siteId);
        if (!optional.isPresent())
            throw new QueenException("site " + siteId + " is not exists!");

        Site site = optional.get();
        if (site.getStatus().equalsIgnoreCase(operation))
            return;

        switch (operation) {
            case Site.SITE_STATUS_INIT :
                site.setStatus(Site.SITE_STATUS_INIT);
                break;
            case Site.SITE_STATUS_START :
                site.setStatus(Site.SITE_STATUS_START);
                break;
            case Site.SITE_STATUS_PAUSE :
                site.setStatus(Site.SITE_STATUS_PAUSE);
                break;
            case Site.SITE_STATUS_STOP :
                site.setStatus(Site.SITE_STATUS_STOP);
                break;
            default:
        }
        // update cache
        sites.put(site.getId(), new SiteManager(site));
        // update db
        repository.save(site);
    }

    public Progress progress(String siteId) throws QueenException {
        if (StringUtils.isEmpty(siteId))
            throw new QueenException("site id can not be empty!");

        return linkService.progress(siteId);
    }

    public long reset(String siteId) throws QueenException {
        if (StringUtils.isEmpty(siteId))
            throw new QueenException("site id can not be empty!");

        return linkService.reset(siteId);
    }
}
