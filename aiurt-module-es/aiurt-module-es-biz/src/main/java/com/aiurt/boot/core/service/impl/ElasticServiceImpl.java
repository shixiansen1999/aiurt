package com.aiurt.boot.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.core.common.model.HighLight;
import com.aiurt.boot.core.common.model.Sort;
import com.aiurt.boot.core.service.ElasticService;
import com.aiurt.boot.core.utils.ElasticTools;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ElasticServiceImpl<T, M> implements ElasticService<T, M> {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;


    @Override
    public Response request(Request request) throws Exception {
        Response response = client.getLowLevelClient().performRequest(request);
        return response;
    }

    @Override
    public boolean save(T t) throws Exception {
        return save(t, null);
    }

    @Override
    public boolean save(T t, String routing) throws Exception {
        String indexName = ElasticTools.getIndexName(t.getClass());
        String id = ElasticTools.getElasticId(t);
        if (StrUtil.isEmpty(indexName)) {
            return false;
        }
        IndexRequest indexRequest = new IndexRequest(indexName);
        if (!StrUtil.isEmpty(id)) {
            indexRequest.id(id);
        }
        String source = JSON.toJSONString(t);
        indexRequest.source(source, XContentType.JSON);
        if (!StrUtil.isEmpty(routing)) {
            indexRequest.routing(routing);
        }
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED
                || indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            return true;
        }
        return false;
    }

    @Override
    public BulkResponse save(List<T> list) throws Exception {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        T t = list.get(0);
        String indexName = ElasticTools.getIndexName(t.getClass());
        BulkResponse bulkResponse = this.storage(list, indexName);
        return bulkResponse;
    }

    @Override
    public BulkResponse[] saveBatch(List<T> list) throws Exception {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        T t = list.get(0);
        String indexName = ElasticTools.getIndexName(t.getClass());
        List<List<T>> lists = ElasticTools.splitList(list, true);
        BulkResponse[] bulkResponses = new BulkResponse[list.size()];
        for (int i = 0; i < lists.size(); i++) {
            bulkResponses[i] = this.storage(lists.get(i), indexName);
        }
        return bulkResponses;
    }

    /**
     * 数据存储
     *
     * @param list      数据集
     * @param indexName 索引名称
     * @return
     * @throws Exception
     */
    private BulkResponse storage(List<T> list, String indexName) throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        for (T t : list) {
            String id = ElasticTools.getElasticId(t);
            String sourceJsonStr = JSON.toJSONString(t);
            bulkRequest.add(new IndexRequest(indexName).id(id).source(sourceJsonStr, XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return bulkResponse;
    }

    @Override
    public IPage<T> search(Page<T> page, QueryBuilder queryBuilder, String[] indexs, Class<T> clazz, HighLight highLight, Sort sort) throws IOException {
        int pageNo = ObjectUtil.isEmpty(page.getCurrent()) ? 1 : (int) page.getCurrent();
        int pageSize = ObjectUtil.isEmpty(page.getSize()) ? 10 : (int) page.getSize();
        IPage<T> pageList = new Page<>(pageNo, pageSize);

        List<T> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indexs);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);

        boolean highLightFlag = false;
        boolean idSortFlag = false;

        // 分页
        if (ObjectUtil.isNotEmpty(page)) {
            int currentPage = (pageNo - 1) * pageSize;
            searchSourceBuilder.from(currentPage);
            searchSourceBuilder.size(pageSize);
        }

        // TODO: 2023/5/16  高亮和排序效果待优化
        // 高亮
        //https://www.elastic.co/guide/en/elasticsearch/reference/7.12/highlighting.html
        //https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.12/java-rest-high-search.html#java-rest-high-search-request-highlighting
        if (ObjectUtil.isNotEmpty(highLight) && ObjectUtil.isNotEmpty(highLight.getHighlightBuilder())) {
            highLightFlag = true;
            searchSourceBuilder.highlighter(highLight.getHighlightBuilder());
        } else if (ObjectUtil.isNotEmpty(highLight) && CollUtil.isNotEmpty(highLight.getHighLightList())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            if (StrUtil.isNotEmpty(highLight.getPreTag()) && StrUtil.isNotEmpty(highLight.getPostTag())) {
                highlightBuilder.preTags(highLight.getPreTag());
                highlightBuilder.postTags(highLight.getPostTag());
            }
            for (int i = 0; i < highLight.getHighLightList().size(); i++) {
                highLightFlag = true;
                // You can set fragment_size to 0 to never split any sentence.
                // 不对高亮结果进行拆分
                highlightBuilder.field(highLight.getHighLightList().get(i), 0);
            }
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        // 排序
        if (ObjectUtil.isNotEmpty(sort)) {
            List<Sort.Order> orders = sort.listOrders();
            for (int i = 0; i < orders.size(); i++) {
                if ("_id".equals(orders.get(i).getProperty())) {
                    idSortFlag = true;
                }
                searchSourceBuilder.sort(new FieldSortBuilder(orders.get(i).getProperty()).order(orders.get(i).getDirection()));
            }
        }
//        //设定searchAfter
//        if (attach.isSearchAfter()) {
//            if (pageSortHighLight == null || pageSortHighLight.getPageSize() == 0) {
//                searchSourceBuilder.size(10);
//            } else {
//                searchSourceBuilder.size(pageSortHighLight.getPageSize());
//            }
//            if (attach.getSortValues() != null && attach.getSortValues().length != 0) {
//                searchSourceBuilder.searchAfter(attach.getSortValues());
//            }
//            // 如果没拼_id的排序，自动添加保证排序唯一性
//            if (!idSortFlag) {
//                Sort.Order order = new Sort.Order(SortOrder.ASC, "_id");
//                pageSortHighLight.getSort().and(new Sort(order));
//                searchSourceBuilder.sort(new FieldSortBuilder("_id").order(SortOrder.ASC));
//            }
//        }
//        //TrackTotalHits设置为true，解除查询结果超出10000的限制
//        if (attach.isTrackTotalHits()) {
//            searchSourceBuilder.trackTotalHits(attach.isTrackTotalHits());
//        }
//
//        //设定返回source
//        if (attach.getExcludes() != null || attach.getIncludes() != null) {
//            searchSourceBuilder.fetchSource(attach.getIncludes(), attach.getExcludes());
//        }
        searchRequest.source(searchSourceBuilder);
//        //设定routing
//        if (!StringUtils.isEmpty(attach.getRouting())) {
//            searchRequest.routing(attach.getRouting());
//        }
//        if (metaData.isPrintLog()) {
//            log.info(searchSourceBuilder.toString());
//        }
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            T t = JSON.parseObject(hit.getSourceAsString(), clazz);
            list.add(t);
        }
        pageList.setRecords(list);
        pageList.setTotal(hits.getTotalHits().value);
        return pageList;
    }

    @Override
    public List<T> search(QueryBuilder queryBuilder, Class<T> clazz, String... indexs) throws IOException {
        List<T> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indexs);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
//        searchSourceBuilder.from(0);
//        searchSourceBuilder.size(200);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            T t = JSON.parseObject(hit.getSourceAsString(), clazz);
            list.add(t);
        }
        return list;
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse;
    }

    @Override
    public BulkResponse bulkUpdate(List<T> list) throws Exception {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        T clazz = list.get(0);
        String indexName = ElasticTools.getIndexName(clazz.getClass());
        BulkRequest bulkRequest = new BulkRequest();
        for (T t : list) {
            String id = ElasticTools.getElasticId(t);
            bulkRequest.add(new UpdateRequest(indexName, id).doc(JSON.toJSONString(t), XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return bulkResponse;
    }

//    private BulkResponse bulkUpdate(List<T> list, String indexName, String indexType) throws Exception {
//        BulkRequest bulkRequest = new BulkRequest();
//        for (T t : list) {
//            String id = ElasticTools.getElasticId(t);
//            if (StrUtil.isNotEmpty(indexType)) {
//                bulkRequest.add(new UpdateRequest(indexName, indexType, id).doc(ElasticTools.getFieldValue(t)));
//            } else {
//                bulkRequest.add(new UpdateRequest(indexName, id).doc(ElasticTools.getFieldValue(t)));
//            }
//        }
//        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//        return bulkResponse;
//    }

    @Override
    public boolean update(T t) throws Exception {
        String indexName = ElasticTools.getIndexName(t.getClass());
        String id = ElasticTools.getElasticId(t);
        if (StrUtil.isEmpty(id)) {
            throw new Exception("ID不能为空！");
        }
        GetRequest request = new GetRequest(indexName, id);
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        if (!response.isExists()) {
            throw new Exception("未找到对应记录！");
        }
        UpdateRequest updateRequest = new UpdateRequest(indexName, id);
        updateRequest.doc(JSON.toJSONString(t), XContentType.JSON);
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
//            log.info("INDEX CREATE SUCCESS");
            return true;
        } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
//            log.info("INDEX UPDATE SUCCESS");
            return true;
        }
        return false;
    }

//    private BulkResponse batchUpdate(List<T> list, String indexName, String indexType, T tot) throws Exception {
//        Map map = ElasticTools.getFieldValue(tot);
//        BulkRequest rrr = new BulkRequest();
//        for (T t : list) {
//            rrr.add(new UpdateRequest(indexName, indexType, ElasticTools.getElasticId(t)).doc(map));
//        }
//        BulkResponse bulkResponse = client.bulk(rrr, RequestOptions.DEFAULT);
//        return bulkResponse;
//    }

    @Override
    public boolean delete(T t) throws Exception {
        return delete(t, null);
    }

    @Override
    public boolean delete(T t, String routing) throws Exception {
        String indexName = ElasticTools.getIndexName(t.getClass());
        String id = ElasticTools.getElasticId(t);
        if (StrUtil.isEmpty(id)) {
            throw new Exception("ID不能为空！");
        }
        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
        if (!StrUtil.isEmpty(routing)) {
            deleteRequest.routing(routing);
        }
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        if (deleteResponse.getResult() == DocWriteResponse.Result.DELETED) {
            log.info("INDEX DELETE SUCCESS");
            return true;
        }
        return false;
    }

    @Override
    public BulkByScrollResponse deleteByCondition(QueryBuilder queryBuilder, Class<T> clazz) throws Exception {
        String indexName = ElasticTools.getIndexName(clazz);
        DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
        request.setQuery(queryBuilder);
        BulkByScrollResponse bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
        return bulkResponse;
    }

    @Override
    public boolean deleteById(M id, Class<T> clazz) throws Exception {
        String indexName = ElasticTools.getIndexName(clazz);
        if (ObjectUtil.isEmpty(id)) {
            throw new Exception("ID不能为空！");
        }
        DeleteRequest deleteRequest = new DeleteRequest(indexName, String.valueOf(id));
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        if (deleteResponse.getResult() == DocWriteResponse.Result.DELETED) {
            log.info("INDEX DELETE SUCCESS");
            return true;
        }
        return false;
    }

    @Override
    public boolean exists(M id, Class<T> clazz) throws Exception {
        String indexName = ElasticTools.getIndexName(clazz);
        if (ObjectUtil.isEmpty(id)) {
            throw new Exception("ID不能为空！");
        }
        GetRequest getRequest = new GetRequest(indexName, String.valueOf(id));
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            return true;
        }
        return false;
    }
}
