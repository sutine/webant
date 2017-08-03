package org.webant.queen.task.entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.GenericGenerator;
import org.webant.commons.entity.SiteConfig;
import org.webant.commons.entity.TaskConfig;
import org.webant.commons.entity.TaskEntity;
import org.webant.queen.site.entity.Site;
import org.webant.queen.utils.JacksonUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Task extends TaskEntity implements Serializable {
    public final static String TASK_STATUS_INIT = "init";
    public final static String TASK_STATUS_START = "start";
    public final static String TASK_STATUS_PAUSE = "pause";
    public final static String TASK_STATUS_STOP = "stop";

    public Task() {
    }

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    @Column(length = 32)
    private String id;
    private String name;
    private String description;
    private Integer priority;
    private String status = TASK_STATUS_INIT;
    private String fingerPrint;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "taskId")
    private List<Site> sites;

    public Task(TaskConfig taskConfig) {
        id = taskConfig.getId();
        name = taskConfig.getName();
        description = taskConfig.getDescription();
        priority = taskConfig.getPriority();
        sites = Arrays.stream(taskConfig.getSites()).map(Site::new).collect(Collectors.toList());

        String json = JacksonUtils.toJson(this);
        fingerPrint = DigestUtils.md5Hex(json);
    }

    public TaskConfig toTaskConfig() {
        TaskConfig taskConfig = new TaskConfig();
        taskConfig.setId(getId());
        taskConfig.setName(getName());
        taskConfig.setDescription(getDescription());
        taskConfig.setPriority(getPriority());
        SiteConfig[] siteConfigs = getSite().stream().map(Site::toSiteConfig).collect(Collectors.toList()).toArray(new SiteConfig[]{});
        taskConfig.setSites(siteConfigs);

        return taskConfig;
    }

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

    public List<Site> getSite() {
        return sites;
    }

    public void setSites(List<Site> sites) {
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
