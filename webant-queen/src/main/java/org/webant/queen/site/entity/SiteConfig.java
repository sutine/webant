package org.webant.queen.site.entity;

import java.io.Serializable;
import java.util.Map;

public class SiteConfig implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private String[] seeds;
    private Integer priority = 4;
    private Long timeInterval = 0L;
    private Long incrementInterval = 0L;
    private HttpConfig http;
    private LinkProvider linkProvider;
    private ProcessorConfig[] processors;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String[] getSeeds() {
        return seeds;
    }

    public void setSeeds(String[] seeds) {
        this.seeds = seeds;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public Long getIncrementInterval() {
        return incrementInterval;
    }

    public void setIncrementInterval(Long incrementInterval) {
        this.incrementInterval = incrementInterval;
    }

    public HttpConfig getHttp() {
        return http;
    }

    public void setHttp(HttpConfig http) {
        this.http = http;
    }

    public LinkProvider getLinkProvider() {
        return linkProvider;
    }

    public void setLinkProvider(LinkProvider linkProvider) {
        this.linkProvider = linkProvider;
    }

    public ProcessorConfig[] getProcessors() {
        return processors;
    }

    public void setProcessors(ProcessorConfig[] processors) {
        this.processors = processors;
    }
}

class HttpConfig implements Serializable {
    String method;
    Integer connectTimeout;
    Integer socketTimeout;
    String encoding;
    Integer retryTimes;
    Integer cycleRetryTimes;
    String body;
    String contentType;
    Boolean proxy;
    Map<String, String> headers;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Integer getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public void setCycleRetryTimes(Integer cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Boolean getProxy() {
        return proxy;
    }

    public void setProxy(Boolean proxy) {
        this.proxy = proxy;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}

class ProcessorConfig implements Serializable {
    String regex;
    HttpConfig http;
    String className;
    StoreProvider[] store;

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public HttpConfig getHttp() {
        return http;
    }

    public void setHttp(HttpConfig http) {
        this.http = http;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public StoreProvider[] getStore() {
        return store;
    }

    public void setStore(StoreProvider[] store) {
        this.store = store;
    }
}

class LinkProvider implements Serializable {
    String className;
    Map<String, String> params;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}

class StoreProvider implements Serializable {
    String className;
    Map<String, String> params;

    public void setClassName(String className) {
        this.className = className;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}