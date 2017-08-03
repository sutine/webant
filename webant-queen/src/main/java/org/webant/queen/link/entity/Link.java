package org.webant.queen.link.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.util.DigestUtils;
import org.webant.queen.utils.DateFormatUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "idx_task_id", columnList = "taskId"),
        @Index(name = "idx_site_id", columnList = "siteId"),
        @Index(name = "idx_node_id", columnList = "nodeId"),
        @Index(name = "idx_status", columnList = "status")})
public class Link extends org.webant.commons.entity.Link {
    @Id
    @Column(length = 64)
    private String id;
    private String taskId;
    private String siteId;
    private String nodeId;
    private String url;
    private String body;
    private String referer;
    private Integer priority = 4;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT, timezone = "GMT+8")
    private Date lastCrawlTime;
    private String status = LINK_STATUS_INIT;
    private Integer dataVersion = 1;
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT, timezone = "GMT+8")
    private Date dataCreateTime = new Date();
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT, timezone = "GMT+8")
    private Date dataUpdateTime = new Date();
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT, timezone = "GMT+8")
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

    public Link(String taskId, String siteId, String url, String status) {
        id = DigestUtils.md5DigestAsHex(url.getBytes());
        this.taskId = taskId;
        this.siteId = siteId;
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
