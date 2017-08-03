package org.webant.commons.entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.webant.commons.utils.JsonUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskEntity implements Serializable {
    public final static String TASK_STATUS_INIT = "init";
    public final static String TASK_STATUS_START = "start";
    public final static String TASK_STATUS_PAUSE = "pause";
    public final static String TASK_STATUS_STOP = "stop";

    private String id;
    private String name;
    private String description;
    private Integer priority;
    private String status = TASK_STATUS_INIT;
    private String fingerPrint;

    private List<SiteEntity> siteEntity;

    public TaskEntity() {
    }

    public TaskEntity(TaskConfig taskConfig) {
        id = taskConfig.getId();
        name = taskConfig.getName();
        description = taskConfig.getDescription();
        priority = taskConfig.getPriority();
        siteEntity = Arrays.stream(taskConfig.getSites()).map(SiteEntity::new).collect(Collectors.toList());

        String json = JsonUtils.toJson(this);
        fingerPrint = DigestUtils.md5Hex(json);
    }

    public TaskConfig toTaskConfig() {
        TaskConfig taskConfig = new TaskConfig();
        taskConfig.setId(getId());
        taskConfig.setName(getName());
        taskConfig.setDescription(getDescription());
        taskConfig.setPriority(getPriority());
        SiteConfig[] siteConfigs = getSiteEntity().stream().map(SiteEntity::toSiteConfig).collect(Collectors.toList()).toArray(new SiteConfig[]{});
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

    public List<SiteEntity> getSiteEntity() {
        return siteEntity;
    }

    public void setSiteEntity(List<SiteEntity> siteEntity) {
        this.siteEntity = siteEntity;
    }
}
