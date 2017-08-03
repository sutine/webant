package org.webant.worker.config;

import java.io.Serializable;
import java.util.List;

public class HttpTaskConfig implements Serializable {
    public final static String TASK_STATUS_INIT = "init";
    public final static String TASK_STATUS_START = "start";
    public final static String TASK_STATUS_PAUSE = "pause";
    public final static String TASK_STATUS_STOP = "stop";

    public HttpTaskConfig() {
    }

    private String id;
    private String name;
    private String description;
    private Integer priority;
    private String status = TASK_STATUS_INIT;
    private String fingerPrint;

    private List<SiteConfig> sites;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<SiteConfig> getSites() {
        return sites;
    }

    public void setSites(List<SiteConfig> sites) {
        this.sites = sites;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }
}
