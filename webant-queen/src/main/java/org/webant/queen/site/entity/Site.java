package org.webant.queen.site.entity;

import org.webant.queen.task.entity.Task;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Site implements Serializable {

    @Id
    @Column(length = 64)
    private String id;
    @ManyToOne
    @JoinColumn(name = "taskId",foreignKey = @ForeignKey(name = "fk_site_task"))
    private Task task;
    private String config;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
