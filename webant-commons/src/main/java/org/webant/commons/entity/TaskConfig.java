package org.webant.commons.entity;

import java.io.Serializable;

public class TaskConfig implements Serializable {
    private String id;
    private String name;
    private String description;
    private Integer priority;
    private SiteConfig[] sites;

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

    public SiteConfig[] getSites() {
        return sites;
    }

    public void setSites(SiteConfig[] sites) {
        this.sites = sites;
    }
}
