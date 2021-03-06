package org.webant.queen.link.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.webant.queen.commons.entity.Progress;
import org.webant.queen.link.dao.LinkRepository;
import org.webant.queen.link.dao.ProgressSummary;
import org.webant.queen.link.entity.Link;
import org.webant.queen.site.entity.Site;
import org.webant.queen.site.service.SiteService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LinkService {
    private static final Logger logger = LoggerFactory.getLogger(LinkService.class);

    @Autowired
    private LinkRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SiteService siteService;

    private String table = "link";

    @Transactional(rollbackFor = {IllegalArgumentException.class})
    public List<Link> select(String nodeId, String status, Pageable pageable) {
        List<Link> seeds = siteService.genSeedLinks();
        if (!seeds.isEmpty())
            return seeds;

        Page<Link> page = repository.selectLinks(status, Site.SITE_STATUS_START, pageable);
        if (page.getContent().isEmpty())
            return new LinkedList<>();

        List<Link> initLinks = page.getContent();

        try {
            List<Link> pendingLinks = initLinks.stream().map(link -> {
                link.setStatus(Link.LINK_STATUS_PENDING);
                return link;
            }).collect(Collectors.toList());
            repository.saveAll(pendingLinks);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return initLinks;
    }

    public Page<Link> list(String status, Pageable pageable) {
        if (StringUtils.isEmpty(status))
            return repository.findAll(pageable);

        return repository.findAllByStatus(status, pageable);
    }

    public Link get(String id) {
        return repository.findById(id).get();
    }

    public int save(List<Link> list) {
        if (list == null || list.isEmpty())
            return 0;

        Iterable<Link> affectRows = repository.saveAll(list);
        return list.size();
    }

    public int save(Link entity) {
        if (entity == null)
            return 0;
        repository.save(entity);
        return 1;
    }

    public int upsert(Link link) {
        if (link == null)
            return 0;

        String sql = "insert into " + table + " (id, task_id, site_id, url, referer, priority, last_crawl_time, status, data_version, data_create_time, " +
                "data_update_time, data_delete_time) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE " +
                "task_id = ?, site_id = ?, url = ?, referer = ?, priority = ?, last_crawl_time = ?, status = ?, data_version = data_version + 1, data_update_time = now()";
        Object[] values = new Object[] {
                link.getId(), link.getTaskId(), link.getSiteId(), link.getUrl(), link.getReferer(), link.getPriority(), link.getLastCrawlTime(),
                link.getStatus(), link.getDataVersion(), link.getDataCreateTime(), link.getDataUpdateTime(), link.getDataDeleteTime(),
                link.getTaskId(), link.getSiteId(), link.getUrl(), link.getReferer(), link.getPriority(), link.getLastCrawlTime(), link.getStatus()
        };

        int affectRowNum = 0;
        try {
            affectRowNum = jdbcTemplate.update(sql, values);
        } catch (Exception e) {
            logger.error("save link failed!", e.getMessage());
        }

        return affectRowNum;
    }

    public int upsert(List<Link> list) {
        if (list == null || list.isEmpty())
            return 0;

        String placeholder = "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        StringBuilder sb = new StringBuilder();
        for (Link ignored : list) {
            sb.append(placeholder).append(", ");
        }
        String placeholders = sb.substring(0, sb.lastIndexOf(", "));

        String sql = "insert into " + table + " (id, task_id, site_id, url, referer, priority, last_crawl_time, status, data_version, " +
                "data_create_time, data_update_time, data_delete_time) values " + placeholders + " ON DUPLICATE KEY UPDATE " +
                "data_version = data_version + 1, data_update_time = now()";

        Object[] values = list.stream().map(link -> new Object[]{
                link.getId(), link.getTaskId(), link.getSiteId(), link.getUrl(), link.getReferer(), link.getPriority(), link.getLastCrawlTime(),
                link.getStatus(), link.getDataVersion(), link.getDataCreateTime(), link.getDataUpdateTime(), link.getDataDeleteTime()
        }).flatMap(objects -> Arrays.stream(objects)).collect(Collectors.toList()).toArray(new Object[]{});

        int affectRowNum = 0;
        try {
            affectRowNum = jdbcTemplate.update(sql, values);
        } catch (Exception e) {
            logger.error("save links failed!", e.getMessage());
        }

        return affectRowNum;
    }

    public int updateStatus(String srcStatus, String dstStatus) {
        String sql = "update " + table + " set status = '" + dstStatus + "' where status = '" + srcStatus + "'";
        int affectRowNum = 0;
        try {
            affectRowNum = jdbcTemplate.update(sql);
        } catch (Exception e) {
            logger.error("update status failed!", e.getMessage());
        }

        return affectRowNum;
    }

    public Progress progress(String siteId) {
        long total = repository.countBySiteId(siteId);
        List<ProgressSummary> list = repository.groupByStatus(siteId);
        Map<String, Integer> map = list.stream()
                .collect(Collectors.toMap(ProgressSummary::getStatus, ProgressSummary::getCount));

        long init = 0;
        long pending = 0;
        long success = 0;
        long fail = 0;
        if (map.containsKey(Link.LINK_STATUS_INIT))
            init = map.get(Link.LINK_STATUS_INIT);

        if (map.containsKey(Link.LINK_STATUS_PENDING))
            pending = map.get(Link.LINK_STATUS_PENDING);

        if (map.containsKey(Link.LINK_STATUS_SUCCESS))
            success = map.get(Link.LINK_STATUS_SUCCESS);

        if (map.containsKey(Link.LINK_STATUS_FAIL))
            fail = map.get(Link.LINK_STATUS_FAIL);

        return new Progress(total, init, pending, success, fail);
    }

    public int reset(String siteId) {
        String sql = "update " + table + " set status = 'init' where site_id = '" + siteId + "'";
        int affectRowNum = 0;
        try {
            affectRowNum = jdbcTemplate.update(sql);
        } catch (Exception e) {
            logger.error("reset site siteId failed!", e.getMessage());
        }

        return affectRowNum;
    }
}
