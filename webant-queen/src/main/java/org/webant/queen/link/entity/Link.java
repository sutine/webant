package org.webant.queen.link.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Link implements Serializable {
    public final static String LINK_STATUS_INIT = "init";
    public final static String LINK_STATUS_PENDING = "pending";
    public final static String LINK_STATUS_SUCCESS = "success";
    public final static String LINK_STATUS_FAIL = "fail";
    @Id
    private String id;
    private String taskId;
    private String siteId;
    private String nodeId;
    private String url;
    private String body;
    private String referer;
    private Integer priority = 4;
    private Date lastCrawlTime;
    private String status = LINK_STATUS_INIT;
    private Integer dataVersion = 1;
    private Date dataCreateTime = new Date();
    private Date dataUpdateTime = new Date();
    private Date dataDeleteTime;

    public Link(String id, String url, String referer, Integer priority, Date lastCrawlTime, String status, Integer dataVersion, Date dataCreateTime, Date dataUpdateTime, Date dataDeleteTime) {
        this.id = id;
        this.url = url;
        this.referer = referer;
        this.priority = priority;
        this.lastCrawlTime = lastCrawlTime;
        this.status = status;
        this.dataVersion = dataVersion;
        this.dataCreateTime = dataCreateTime;
        this.dataUpdateTime = dataUpdateTime;
        this.dataDeleteTime = dataDeleteTime;
    }

    public Link(String id, String taskId, String siteId, String url, String referer, Date lastCrawlTime) {
        this.id = id;
        this.taskId = taskId;
        this.siteId = siteId;
        this.url = url;
        this.referer = referer;
        this.lastCrawlTime = lastCrawlTime;
    }

    public Link(String id, String url, String status) {
        this.id = id;
        this.url = url;
        this.status = status;
    }

    public Link() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getLastCrawlTime() {
        return lastCrawlTime;
    }

    public void setLastCrawlTime(Date lastCrawlTime) {
        this.lastCrawlTime = lastCrawlTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(Integer dataVersion) {
        this.dataVersion = dataVersion;
    }

    public Date getDataCreateTime() {
        return dataCreateTime;
    }

    public void setDataCreateTime(Date dataCreateTime) {
        this.dataCreateTime = dataCreateTime;
    }

    public Date getDataUpdateTime() {
        return dataUpdateTime;
    }

    public void setDataUpdateTime(Date dataUpdateTime) {
        this.dataUpdateTime = dataUpdateTime;
    }

    public Date getDataDeleteTime() {
        return dataDeleteTime;
    }

    public void setDataDeleteTime(Date dataDeleteTime) {
        this.dataDeleteTime = dataDeleteTime;
    }
}
