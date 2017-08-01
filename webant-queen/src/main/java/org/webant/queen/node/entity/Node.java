package org.webant.queen.node.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Node implements Serializable {
    public final static String NODE_STATUS_INIT = "init";
    public final static String NODE_STATUS_RUNNING = "running";
    public final static String NODE_STATUS_BUSY = "busy";
    public final static String NODE_STATUS_HEAVY = "heavy";
    public final static String NODE_STATUS_OFFLINE = "offline";
    public final static String NODE_STATUS_DISABLE = "disable";
    public final static String NODE_STATUS_SHUTDOWN = "shutdown";

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    @Column(length = 32)
    private String id;
    private String status = NODE_STATUS_INIT;
    private Integer dataVersion = 1;
    private Date dataCreateTime = new Date();
    private Date dataUpdateTime = new Date();
    private Date dataDeleteTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
