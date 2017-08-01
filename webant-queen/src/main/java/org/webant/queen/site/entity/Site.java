package org.webant.queen.site.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Site implements Serializable {
    public final static String SITE_STATUS_INIT = "init";
    public final static String SITE_STATUS_START = "start";
    public final static String SITE_STATUS_PAUSE = "pause";
    public final static String SITE_STATUS_STOP = "stop";

    public Site() {
    }

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    @Column(length = 32)
    private String id;

    @Column(columnDefinition = "text")
    private String config;

    private String status = SITE_STATUS_INIT;

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
}
