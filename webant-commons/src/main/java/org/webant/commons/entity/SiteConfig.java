package org.webant.commons.entity;

import java.io.Serializable;
import java.util.Map;

public class SiteConfig implements Serializable {
    public String id;
    public String name;
    public String description;
    public String[] seeds;
    public Integer priority = 4;
    public Long timeInterval = 0L;
    public Long incrementInterval = 0L;
    public HttpConfig http;
    public LinkProvider linkProvider;
    public ProcessorConfig[] processors;

    public static class HttpConfig implements Serializable {
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

    public static class ProcessorConfig implements Serializable {
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

    public static class LinkProvider implements Serializable {
        String className;
        Map<String, Object> params;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }

    public static class StoreProvider implements Serializable {
        String className;
        Map<String, Object> params;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
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
