package org.webant.commons.entity;

import java.io.Serializable;

public class SiteEntity implements Serializable {
    public final static String SITE_STATUS_INIT = "init";
    public final static String SITE_STATUS_START = "start";
    public final static String SITE_STATUS_PAUSE = "pause";
    public final static String SITE_STATUS_STOP = "stop";

    public SiteEntity() {
    }

    private String id;
    private String config;
    private String status = SITE_STATUS_INIT;
    private TaskEntity task;

    public SiteEntity(String config) {
        this.config = config;
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
