package org.webant.queen.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Data implements Serializable {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    @Column(length = 32)
    private String id;
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
