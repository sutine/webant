package org.webant.extension.elasticsearch;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webant.commons.annotation.DocId;
import org.webant.commons.annotation.DocParentId;
import org.webant.commons.annotation.Index;
import org.webant.commons.annotation.Type;
import org.webant.commons.entity.HttpDataEntity;
import org.webant.commons.utils.BeanUtils;
import org.webant.commons.utils.JsonUtils;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ElasticSearchUtils<T extends HttpDataEntity> {
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchUtils.class);
    private Client client;
    private long timeout = 5000;

    public boolean init(String clusterName, String host, int port) throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.ping_timeout", "10s")
                .put("transport.ping_schedule", "5s")
                .build();

        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));

        return true;
    }

    public T get(String id) {
        if (StringUtils.isBlank(id))
            return null;

        GetRequest getRequest = new GetRequest().id(id);
        String source = client.get(getRequest).actionGet(timeout).getSourceAsString();
        T data = JsonUtils.fromJson(source, new TypeToken<T>() {}.getType());
        return data;
    }

    private IndexRequest buildIndexRequest(T data, Boolean isRefresh) throws ElasticSearchException {
        if (data == null)
            throw new ElasticSearchException("data can not be null！");
        String id = getDocId(data);
        if (StringUtils.isBlank(id))
            throw new ElasticSearchException("data id can not be empty！");

        String json = JsonUtils.toJson(data);
        String parentId = getDocParentId(data);

        IndexRequest indexRequest = new IndexRequest(getIndex(data), getType(data))
                .source(json, XContentFactory.xContentType(json));
        if (isRefresh) indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        if (StringUtils.isNotBlank(parentId)) indexRequest.parent(parentId);
        if (StringUtils.isNotBlank(id)) indexRequest.id(id);
        return indexRequest;
    }

    public Integer upsert(T data, Boolean isRefresh) throws ExecutionException, InterruptedException, ElasticSearchException {
        if (data == null)
            throw new ElasticSearchException("data can not be null！");
        String id = getDocId(data);
        if (StringUtils.isBlank(id))
            throw new ElasticSearchException("data id can not be empty！");

        String json = JsonUtils.toJson(data);
        IndexRequest indexRequest = buildIndexRequest(data, isRefresh);

        UpdateRequest updateRequest = new UpdateRequest(getIndex(data), getType(data), getDocId(data))
                .doc(json, XContentFactory.xContentType(json))
                .upsert(indexRequest);

//        if (isRefresh) updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        long version = client.update(updateRequest).get().getVersion();
        if (version > 0)
            return 1;
        else
            return 0;
    }

    public int upsert(List<T> list, Boolean isRefresh) throws ExecutionException, InterruptedException, ElasticSearchException {
        if (list == null || list.isEmpty())
            return 0;

        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (T data: list) {
            IndexRequest indexRequest = buildIndexRequest(data, isRefresh);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = bulkRequest.get(TimeValue.timeValueMillis(timeout));
        if (bulkResponse.hasFailures()) {
            logger.error("batch save failed! error" + bulkResponse.buildFailureMessage());
        }

        return bulkResponse.getItems().length;
    }

    public int save(T data, boolean isRefresh) throws ElasticSearchException {
        IndexRequest indexRequest = buildIndexRequest(data, isRefresh);
        long version = client.index(indexRequest).actionGet(timeout).getVersion();
        if (version > 0)
            return 1;
        else
            return 0;
    }

    public int update(List<T> list, Boolean isRefresh) throws ExecutionException, InterruptedException, ElasticSearchException {
        if (list == null || list.isEmpty())
            return 0;

        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (T data: list) {
            UpdateRequest updateRequest = buildUpdateRequest(data, isRefresh);
            bulkRequest.add(updateRequest);
        }

        BulkResponse bulkResponse = bulkRequest.get(TimeValue.timeValueMillis(timeout));
        if (bulkResponse.hasFailures()) {
            logger.error("batch update failed! error" + bulkResponse.buildFailureMessage());
        }

        return bulkResponse.getItems().length;
    }

    private UpdateRequest buildUpdateRequest(T data, Boolean isRefresh) throws ElasticSearchException {
        if (data == null)
            throw new ElasticSearchException("data can not be null！");
        String id = getDocId(data);
        if (StringUtils.isBlank(id))
            throw new ElasticSearchException("data id can not be empty！");

        String parentId = getDocParentId(data);

        IndexRequest indexRequest = buildIndexRequest(data, isRefresh);
        UpdateRequest updateRequest = new UpdateRequest(getIndex(data), getType(data), id).upsert(indexRequest);
        if (isRefresh) updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        if (StringUtils.isNotBlank(parentId)) updateRequest.parent(parentId);
        return updateRequest;
    }

    private String getIndex(T data) {
        if (data == null)
            return "";

        Class clazz = data.getClass();
        if (clazz.isAnnotationPresent(Index.class)) {
            Index annotation = data.getClass().getAnnotation(Index.class);
            return annotation.value();
        } else
            return "";
    }

    private String getType(T data) {
        if (data == null)
            return "";

        Class clazz = data.getClass();
        if (clazz.isAnnotationPresent(Type.class)) {
            Type annotation = data.getClass().getAnnotation(Type.class);
            return annotation.value();
        } else
            return "";
    }

    private String getDocId(T data) throws ElasticSearchException {
        if (data == null)
            return "";

        Field[] fields = BeanUtils.getDeclaredFields(data.getClass());
        for (Field field : fields) {
            DocId annotation = field.getAnnotation(DocId.class);
            if (annotation != null)
                try {
                    return BeanUtils.getProperty(data, field.getName());
                } catch (Exception e) {
                    throw new ElasticSearchException("get data id failed! error: " + e.getMessage());
                }
        }

        return "";
    }

    private String getDocParentId(T data) {
        if (data == null)
            return null;

        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            DocParentId annotation = field.getAnnotation(DocParentId.class);
            if (annotation != null)
                return annotation.value();
        }

        return null;
    }
}

