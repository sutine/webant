package org.webant.queen.site.service;

import org.springframework.util.StringUtils;
import org.webant.queen.link.entity.Link;
import org.webant.queen.site.entity.Site;
import org.webant.commons.entity.SiteConfig;
import org.webant.queen.utils.JsonUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SiteManager {
    private Site site;
    private SiteConfig config;
    private long nextCrawlTime = 0;

    public SiteManager(SiteConfig config) {
        this.config = config;
    }

    public SiteManager(Site site) {
        this.site = site;

        if (!StringUtils.isEmpty(site.getConfig()))
            config = JsonUtils.fromJson(site.getConfig(), SiteConfig.class);
    }

    public List<Link> genSeedLinks() {
        List<Link> links = new LinkedList<>();
        long now = System.currentTimeMillis();
        if (site.getStatus().equalsIgnoreCase(Site.SITE_STATUS_START) && now >= nextCrawlTime) {
            if (config == null)
                return links;

            if (config.getIncrementInterval() == null || config.getIncrementInterval() > 0) {
                nextCrawlTime = now + config.getIncrementInterval();

                links = Arrays.stream(config.getSeeds())
                        .map(seed -> new Link(site.getTask().getId(), site.getId(), seed, Link.LINK_STATUS_INIT))
                        .collect(Collectors.toList());
            }
        }
        return links;
    }

}
