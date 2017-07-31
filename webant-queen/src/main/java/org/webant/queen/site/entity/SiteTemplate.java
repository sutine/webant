package org.webant.queen.site.entity;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(indexes = {@Index(name = "idx_fingerprint", columnList = "fingerPrint")})
public class SiteTemplate implements Serializable {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    @Column(length = 32)
    private String id;
    @Column(columnDefinition = "text")
    private String config;
    private String fingerPrint;

    public SiteTemplate() {
    }

    public SiteTemplate(String config) {
        this.config = config;
        if (!StringUtils.isEmpty(config))
            fingerPrint = DigestUtils.md5DigestAsHex(config.getBytes());
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

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }
}
