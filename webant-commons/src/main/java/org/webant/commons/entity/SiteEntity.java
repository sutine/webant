package org.webant.commons.entity;

import org.apache.commons.lang3.StringUtils;
import org.webant.commons.utils.JsonUtils;

import java.io.Serializable;

public class SiteEntity implements Serializable {
    public final static String SITE_STATUS_INIT = "init";
    public final static String SITE_STATUS_START = "start";
    public final static String SITE_STATUS_PAUSE = "pause";
    public final static String SITE_STATUS_STOP = "stop";

    protected String id;
    protected String name;
    protected String description;
    protected Integer priority;
    protected String config;
    protected String status = SITE_STATUS_INIT;
    protected TaskEntity task;

    public SiteEntity() {
    }

    public SiteEntity(SiteConfig siteConfig) {
        name = siteConfig.name;
        description = siteConfig.description;
        priority = siteConfig.priority;
        config = JsonUtils.toJson(siteConfig);
    }

    public SiteConfig toSiteConfig() {
        if (StringUtils.isBlank(config))
            return null;

        return JsonUtils.fromJson(config, SiteConfig.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
