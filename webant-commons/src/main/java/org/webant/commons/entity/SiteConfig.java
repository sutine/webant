package org.webant.commons.entity;

import java.io.Serializable;
import java.util.Map;

public class SiteConfig implements Serializable {
    String id;
    String name;
    String description;
    String[] seeds;
    Integer priority = 4;
    Long interval = 0L;
    Long incrementInterval = 0L;
    HttpConfig http;
    LinkProvider linkProvider;
    ProcessorConfig[] processors;
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
}

class ProcessorConfig implements Serializable {
    String regex;
    HttpConfig http;
    String className;
    StoreProvider[] store;
}

class LinkProvider implements Serializable {
    String className;
    Map<String, String> params;
}

class StoreProvider implements Serializable {
    String className;
    Map<String, String> params;
}