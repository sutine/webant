package org.webant.queen.site.entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.webant.commons.entity.SiteConfig;
import org.webant.commons.entity.SiteEntity;
import org.webant.commons.utils.JsonUtils;
import org.webant.queen.task.entity.Task;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Site extends SiteEntity implements Serializable {
    public final static String SITE_STATUS_INIT = "init";
    public final static String SITE_STATUS_START = "start";
    public final static String SITE_STATUS_PAUSE = "pause";
    public final static String SITE_STATUS_STOP = "stop";

    public Site() {
    }

    public Site(SiteConfig siteConfig) {
        name = siteConfig.getName();
        description = siteConfig.getDescription();
        priority = siteConfig.getPriority();
        config = JsonUtils.toJson(siteConfig);
    }

    public SiteConfig toSiteConfig() {
        if (StringUtils.isBlank(config))
            return null;

        return JsonUtils.fromJson(config, SiteConfig.class);
    }

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    @Column(length = 32)
    private String id;

    @Column(columnDefinition = "text")
    private String config;

    private String status = SITE_STATUS_INIT;

    @ManyToOne
    @JoinColumn(name = "taskId",foreignKey = @ForeignKey(name = "fk_site_task"))
    private Task task;

    public Site(String config) {
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
