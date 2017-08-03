package org.webant.queen.task.vo;

import org.springframework.util.DigestUtils;
import org.webant.queen.site.entity.Site;
import org.webant.commons.entity.SiteConfig;
import org.webant.queen.task.entity.Task;
import org.webant.queen.utils.JacksonUtils;
import org.webant.queen.utils.JsonUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class TaskVo implements Serializable {

    private String id;
    private String name;
    private String description;
    private Integer priority;
    private List<SiteConfig> sites;

    public static TaskVo fromTask(Task task) {
        return new TaskVo(task);
    }

    public TaskVo(Task task) {
        id = task.getId();
        name = task.getName();
        description = task.getDescription();
        priority = task.getPriority();
        sites = task.getSites().stream().map(site -> JsonUtils.fromJson(site.getConfig(), SiteConfig.class)).collect(Collectors.toList());
    }

    public Task toTask() {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setDescription(description);
        task.setPriority(priority);
        List<Site> list = sites.stream().map(config -> new Site(JacksonUtils.toJson(config))).collect(Collectors.toList());
        task.setSites(list);

        String json = JacksonUtils.toJson(task);
        String fingerPrint = DigestUtils.md5DigestAsHex(json.getBytes());
        task.setFingerPrint(fingerPrint);

        return task;
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

    public List<SiteConfig> getSites() {
        return sites;
    }

    public void setSites(List<SiteConfig> sites) {
        this.sites = sites;
    }
}
