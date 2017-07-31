package org.webant.queen.task.entity;

import org.hibernate.annotations.GenericGenerator;
import org.webant.queen.site.entity.Site;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Task implements Serializable {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    @Column(length = 32)
    private String id;
    private String name;
    private String description;
    private Integer priority;
    private String fingerPrint;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "taskId")
    private List<Site> sites;

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

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }
}
