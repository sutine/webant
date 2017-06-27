package org.webant.commons.test.es;

import com.google.gson.*;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.*;

public class ESClientUtils {
    private static Logger logger = LoggerFactory.getLogger(ESClientUtils.class);
    private static Client client;
    private static Gson gson = null;
    static {
         gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .setDateFormat(DateFormat.LONG)
                .create();
    }
    public static boolean init(String clusterName, String host, int port) {
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.ping_timeout", "10s")
                .put("transport.ping_schedule", "5s")
                .build();

        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(addr, port));

        return true;
    }

    public static void index(String index, String type, HashMap<String, Map<String, String>> data) {
        JsonObject o = new JsonObject();
        for (String key : data.keySet()) {
            if (key == null || key.isEmpty()) {
                continue;
            }
            for (String k : data.get(key).keySet()) {
                if (k == null || k.isEmpty()) {
                    continue;
                }
                o.addProperty(k, data.get(key).get(k));
            }
        }

    }
    //批量索引数据，数据的_id字段由es自动生成
    public static void index(String index, String type, ArrayList<String> data) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for(int i=0; i<data.size(); i++){
            IndexRequestBuilder requestBuilder = client.prepareIndex(index, type);
            requestBuilder.setSource(data.get(i));
            bulkRequest.add(requestBuilder);
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            logger.error("批量提交出错！错误信息：" + bulkResponse.buildFailureMessage());
        }
    }

    public static void index(String index, String type, Map<String, String> data) {
        index(index, type, data, null);
    }

    //批量索引数据，数据的_id字段不由es自动生成，而是由data中的key字段显式指定
    //如果该数据有parent，它的parentId由parentId指定
    //索引成功之后，在查询时可通过has_parent，has_child等语法实时关联查询
    public static void index(String index, String type, Map<String, String> data, String parentId) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (String key: data.keySet()) {
            IndexRequestBuilder requestBuilder = client.prepareIndex(index, type);
            if (parentId != null && !parentId.isEmpty()) {
                requestBuilder.setParent(parentId);
            }
            requestBuilder.setId(key);
            requestBuilder.setSource(data.get(key));
            bulkRequest.add(requestBuilder);
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            logger.error("批量提交出错！错误信息：" + bulkResponse.buildFailureMessage());
        }
    }

    public static void index(String index, String type, List<DataEntity> data) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (DataEntity item: data) {
            if (item.getJson() == null || item.getJson().isEmpty()) {
                continue;
            }
            IndexRequestBuilder requestBuilder = client.prepareIndex(index, type);
            if (item.getParentId() != null && !item.getParentId().isEmpty()) {
                requestBuilder.setParent(item.getParentId());
            }
            if (item.getId() != null && !item.getId().isEmpty()) {
                requestBuilder.setId(item.getId());
            }
            requestBuilder.setSource(item.getJson());
            bulkRequest.add(requestBuilder);
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            logger.error("批量提交出错！错误信息：" + bulkResponse.buildFailureMessage());
        }
    }

    public static String toJson(Object o) {
        if (gson == null || o == null) {
            return "";
        }
        return gson.toJson(o);
    }

    //索引单条数据
    public static void index(String index, String type, String json) {
        index(index, type, null, null, json, false);
    }

    public static void index(String index, String type, String id, String parentId, String json) {
        index(index, type, id, parentId, json, false);
    }

    public static void index(String index, String type, String id, String json) {
        index(index, type, id, null, json, false);
    }

    public static void index(String index, String type, String id, String parentId, String json, boolean isRefresh) {
        if (json == null || json.isEmpty()) {
            return;
        }
        //调用.setRefresh(true)设置实时索引，即该doc一提交马上能被搜索到
        IndexRequestBuilder request = client.prepareIndex(index, type);
        request.setSource(json);
        if (isRefresh)
            request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        if (parentId != null && !parentId.isEmpty()) {
            request.setParent(parentId);
        }
        if (id != null && !id.isEmpty()) {
            request.setId(id);
        }

        request.execute().actionGet();
    }

    public static void update(String index, String type, List<DataEntity> data) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (DataEntity item: data) {
            if (item.getJson() == null || item.getJson().isEmpty()) {
                continue;
            }
            UpdateRequestBuilder requestBuilder = client.prepareUpdate(index, type, item.getId());
            if (item.getParentId() != null && !item.getParentId().isEmpty()) {
                requestBuilder.setParent(item.getParentId());
            }
            requestBuilder.setDoc(item.getJson());
            bulkRequest.add(requestBuilder);
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            logger.error("批量提交出错！错误信息：" + bulkResponse.buildFailureMessage());
        }
    }

    public static void update(String index, String type, String id, String json) {
        update(index, type, id, null, json, false);
    }

    public static void update(String index, String type, String id, String parentId, String json, boolean isRefresh) {
        if (json == null || json.isEmpty()) {
            return;
        }
        //调用.setRefresh(true)设置实时索引，即该doc一提交马上能被搜索到
        UpdateRequestBuilder request = client.prepareUpdate(index, type, id);
        request.setDoc(json);
        if (isRefresh)
            request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        if (parentId != null && !parentId.isEmpty()) {
            request.setParent(parentId);
        }
        if (id != null && !id.isEmpty()) {
            request.setId(id);
        }

        request.execute().actionGet();
    }

    //使用es的scan和scroll特性，从已有索引中高效取得源数据，并把数据索引到另一个索引中
    public static void moveIndex(String srcInxex, String destIndex, String destType, int batchSize) {
        Map<String, String> data = new HashMap();
        SearchResponse scrollResp = client.prepareSearch(srcInxex)
//                .setSearchType(SearchType.SCAN)
                .setScroll(TimeValue.timeValueMinutes(8))
                .setSize(batchSize)
                .execute().actionGet();

        scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                .setScroll(TimeValue.timeValueMinutes(8))
                .execute().actionGet();
        long startTime = System.currentTimeMillis();
        long endTime = startTime;

        while (scrollResp.getHits().hits().length != 0) {
            for (SearchHit hit : scrollResp.getHits()) {
                data.put((String)hit.getSource().get("para_id"), hit.getSourceAsString());
            }
            index(destIndex, destType, data);
            endTime = System.currentTimeMillis();
            long elapse = endTime - startTime;
            logger.info("索引一批数据完成，数据量 " + data.size() + " ，耗时 " + elapse + " ms，平均耗时： " + (elapse * 1000)/data.size() + " us。");
            startTime = endTime;
            data = new HashMap();

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(TimeValue.timeValueMinutes(8))
                    .execute().actionGet();
        }
    }

    //构造查询条件，对指定的索引和类型进行检索，得到需要的数据
    public static void search(String index, String type) {
        String script = "(doc['qrcode_w'].value * doc['qrcode_h'].value) / (doc['width'].value * doc['height'].value) > 0.5";
        QueryBuilder resQuery =  QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("width").from(10))
                .filter(QueryBuilders.scriptQuery(new Script(script)));
        QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("text", "点击"))
                .must(QueryBuilders.termQuery("text", "二维码"))
                .must(QueryBuilders.termQuery("text", "关注"))
                .must(QueryBuilders.rangeQuery("total").from(5))
//                .must(QueryBuilders.hasChildQuery("para_res", resQuery))

                .mustNot(QueryBuilders.termQuery("is_head", true))
                .mustNot(QueryBuilders.termQuery("is_tail", true))

                .should(QueryBuilders.termQuery("text", "查看"));

        SearchRequestBuilder request = client.prepareSearch()
                .setIndices(index)
                .setTypes(type)
                .setQuery(query)
                .setFrom(0)
                .setSize(40)
                .addSort(SortBuilders.scriptSort(new Script("Math.random()"), ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.ASC))
                .addAggregation(AggregationBuilders.range("group_by_total").field("total").addRange(0, 50).addRange(50, 100).addRange(100, Double.MAX_VALUE))
                .addAggregation(AggregationBuilders.dateRange("group_by_update").field("record_update_time").format("yyyy-MM-dd HH:mm:ss").addRange("now-1M", "now-3d").addRange("now-3d", "now-2d").addRange("now-2d", "now-1d").addRange("now-1d", "now"));
//                .addHighlightedField("text").setHighlighterPreTags("<font color='red'>").setHighlighterPostTags("</font>");

        System.out.println(request.toString());

        SearchResponse response = request.execute().actionGet();

        if (response.getHits().hits().length != 0) {
            for (SearchHit hit : response.getHits().hits()) {
                System.out.println(hit.getSourceAsString());
            }
        }
    }

    //接收一串json格式的查询串，检索数据
    public static void search(String index, String type, String queryString) {
        SearchRequestBuilder request = client.prepareSearch()
                .setIndices(index)
                .setTypes(type);
//                .setQuery(queryString);

        System.out.println(request.toString());

        SearchResponse response = request.execute().actionGet();

        if (response.getHits().hits().length != 0) {
            for (SearchHit hit : response.getHits().hits()) {
                System.out.println(hit.getSourceAsString());
            }
        }
    }

    public static class DateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }
    }
    public static class DateSerializer implements JsonSerializer<Date> {
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime());
        }
    }

    public static void main(String[] args) {
        String SRC_INDEX = "es_etl_wx_para";
        String DEST_INDEX = "oss_wx_para_rel";
        String DEST_TYPE = "para";
        int BATCH_SIZE = 1000;
        String ES_CLUSTER_NAME = "pinme";
        String ES_HOST = "howpay-52";
        int ES_HOST_PORT  = 9300;

        ESClientUtils.init(ES_CLUSTER_NAME, ES_HOST, ES_HOST_PORT);
//        moveIndex(SRC_INDEX, DEST_INDEX, DEST_TYPE, BATCH_SIZE);
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"term\": {\n" +
                "            \"text\": \"点击\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"term\": {\n" +
                "            \"text\": \"二维码\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"term\": {\n" +
                "            \"text\": \"关注\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"range\": {\n" +
                "            \"total\": {\n" +
                "              \"gte\": \"5\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"has_child\": {\n" +
                "            \"type\": \"para_res\",\n" +
                "            \"query\": {\n" +
                "              \"bool\": {\n" +
                "                \"must\": [\n" +
                "                  {\n" +
                "                    \"range\": {\n" +
                "                      \"width\": {\n" +
                "                        \"gte\": \"10\"\n" +
                "                      }\n" +
                "                    }\n" +
                "                  }\n" +
                "                ],\n" +
                "                \"filter\": {\n" +
                "                  \"script\": {\n" +
                "                    \"script\": \"(doc['qrcode_w'].value * doc['qrcode_h'].value) / (doc['width'].value * doc['height'].value) > 0.5\"\n" +
                "                  }\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"must_not\": [\n" +
                "        {\n" +
                "          \"term\": {\n" +
                "            \"is_head\": \"true\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"term\": {\n" +
                "            \"is_tail\": \"true\"\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"should\": [\n" +
                "        {\n" +
                "          \"term\": {\n" +
                "            \"text\": \"查看\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"from\": 0,\n" +
                "  \"size\": 10,\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"_script\": {\n" +
                "        \"script\": \"Math.random()\",\n" +
                "        \"type\": \"number\",\n" +
                "        \"params\": {},\n" +
                "        \"order\": \"asc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"aggs\": {\n" +
                "    \"group_by_total\": {\n" +
                "      \"range\": {\n" +
                "        \"field\": \"total\",\n" +
                "        \"ranges\": [\n" +
                "          {\n" +
                "            \"from\": 0,\n" +
                "            \"to\": 50\n" +
                "          },\n" +
                "          {\n" +
                "            \"from\": 50,\n" +
                "            \"to\": 100\n" +
                "          },\n" +
                "          {\n" +
                "            \"from\": 100\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    \"group_by_update\": {\n" +
                "      \"range\": {\n" +
                "        \"field\": \"record_update_time\",\n" +
                "        \"ranges\": [\n" +
                "          {\n" +
                "            \"from\": \"now-1M\",\n" +
                "            \"to\": \"now-3d\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"from\": \"now-3d\",\n" +
                "            \"to\": \"now-2d\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"from\": \"now-2d\",\n" +
                "            \"to\": \"now-1d\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"from\": \"now-1d\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"highlight\": {\n" +
                "    \"fields\": {\n" +
                "      \"text\": {}\n" +
                "    },\n" +
                "    \"pre_tags\": [\n" +
                "      \"<font color=red>\"\n" +
                "    ],\n" +
                "    \"post_tags\": [\n" +
                "      \"</font>\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        search(DEST_INDEX, DEST_TYPE);
    }

    public class DataEntity {
        private String id;
        private String parentId;
        private String json;

        public DataEntity(String id, String parentId, String json) {
            this.id = id;
            this.parentId = parentId;
            this.json = json;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }
    }
}

