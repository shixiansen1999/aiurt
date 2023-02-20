package com.aiurt.boot.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ElasticsearchClientUtil {

    @Value("${spring.elasticsearch.connection-timeout}")
    private String timeout;

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    /**
     * 创建索引
     *
     * @param index
     */
    public CreateIndexResponse createIndex(String index) {
        CreateIndexResponse response = this.createIndex(index, false);
        return response;
    }

    /**
     * 创建索引(是否删掉原来的索引重建)
     *
     * @param index
     * @param flag
     * @return
     */
    public CreateIndexResponse createIndex(String index, boolean flag) {
        if (flag) {
            this.deleteIndex(index);
        }
        CreateIndexRequest request = new CreateIndexRequest(index);
        try {
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            log.error("索引【{}】创建失败！", index, e);
            e.printStackTrace();
            return new CreateIndexResponse(false, false, index);
        }
    }

    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    public boolean isIndexExists(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("索引【{}】不存在！", index, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除索引
     *
     * @param index
     */
    public AcknowledgedResponse deleteIndex(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response;
        } catch (Exception e) {
            log.error("删除索引【{}】失败！", index, e.getMessage());
            e.printStackTrace();
            return new AcknowledgedResponse(false);
        }
    }

    /**
     * 创建文档
     *
     * @param index
     * @param data
     */
    public void createDocument(String index, JSONObject data) {
        IndexRequest request = new IndexRequest(index);
        request.timeout(timeout);
        request.source(data.toJSONString(), XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("创建文档失败！", e);
            e.printStackTrace();
        }
    }

    /**
     * 修改文档
     *
     * @param index
     * @param id
     * @param data
     */
    public void updateDocument(String index, String id, JSONObject data) {
        UpdateRequest request = new UpdateRequest(index, id);
        request.doc(data.toJSONString(), XContentType.JSON);
        try {
            client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("修改文档失败！", e);
            e.printStackTrace();
        }
    }

    /**
     * 删除文档
     *
     * @param index
     * @param id
     */
    public void deleteDocument(String index, String id) {
        DeleteRequest request = new DeleteRequest(index, id);
        try {
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除文档失败！", e);
            e.printStackTrace();
        }
    }

    /**
     * 批量插入文档
     *
     * @param index
     * @param data
     */
    public void createBulkDocument(String index, List<?> data) {
        BulkRequest request = new BulkRequest();
        for (Object obj : data) {
            request.add(new IndexRequest(index).source(JSON.toJSONString(obj)));
        }
        try {
            BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("批量插入文档存在异常！", e);
            e.printStackTrace();
        }
    }

    /**
     * 批量更新文档
     */
    public void updateBulkDocument(String index, List<?> data) {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest(index).source(JSON.toJSONString(data)));
        try {
            BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("批量更新文档存在异常！", e);
            e.printStackTrace();
        }
    }

    /**
     * 查询文档
     *
     * @param index
     * @param builder
     */
    public SearchResponse queryDocument(String index, SearchSourceBuilder builder) {
        SearchRequest request = new SearchRequest(index);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
