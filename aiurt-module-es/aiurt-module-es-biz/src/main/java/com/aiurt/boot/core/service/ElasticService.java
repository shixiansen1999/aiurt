package com.aiurt.boot.core.service;

import com.aiurt.boot.core.common.model.HighLight;
import com.aiurt.boot.core.common.model.Sort;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ElasticService<T, M> {

    /**
     * 通过Low Level REST Client 查询
     *
     * @param request 原生查询对象
     * @return
     * @throws Exception
     */
    Response request(Request request) throws Exception;

    /**
     * 新增索引
     *
     * @param t 索引实体类
     */
    boolean save(T t) throws Exception;

    /**
     * 新增索引（路由方式）
     *
     * @param t       索引实体类
     * @param routing 路由信息（默认路由为索引数据_id）
     * @return
     * @throws Exception
     */
    boolean save(T t, String routing) throws Exception;

    /**
     * 新增索引集合
     *
     * @param list 索引实体类
     */
    BulkResponse save(List<T> list) throws Exception;

    /**
     * 新增索引集合（分批保存，每批默认5000条数据）
     *
     * @param list 索引实体类
     */
    BulkResponse[] saveBatch(List<T> list) throws Exception;

    /**
     * 支持分页、高亮、排序的查询
     *
     * @param page         分页参数对象
     * @param queryBuilder 查询条件
     * @param indexs       索引名称
     * @param clazz        索引实体类
     * @param highLight    高亮参数对象
     * @param sort         排序参数对象
     * @return
     * @throws IOException
     */
    IPage<T> search(Page<T> page, QueryBuilder queryBuilder, String[] indexs, Class<T> clazz, HighLight highLight, Sort sort) throws IOException;

    /**
     * 非分页查询(跨索引)
     *
     * @param queryBuilder 查询条件
     * @param clazz        索引实体类
     * @param indexs       索引名称
     * @return
     * @throws Exception
     */
    List<T> search(QueryBuilder queryBuilder, Class<T> clazz, String... indexs) throws Exception;

    /**
     * 原生查询
     *
     * @param searchRequest 原生查询请求对象
     * @return
     * @throws Exception
     */
    SearchResponse search(SearchRequest searchRequest) throws Exception;

    /**
     * 更新索引集合
     *
     * @param list 索引实体类集合
     * @return
     * @throws Exception
     */
    public BulkResponse bulkUpdate(List<T> list) throws Exception;

    /**
     * 更新索引
     *
     * @param t 索引实体类
     */
    public boolean update(T t) throws Exception;

    /**
     * 根据Map更新文档
     *
     * @param docId  文档ID
     * @param clazz  索引实体类
     * @param source 更新数据内容
     */
    boolean update(String docId, Class<T> clazz, Map<String, Object> source) throws IOException;

    /**
     * 删除索引
     *
     * @param t 索引实体类
     */
    public boolean delete(T t) throws Exception;

    /**
     * 删除索引（路由方式）
     *
     * @param t       索引实体类
     * @param routing 路由信息（默认路由为索引数据_id）
     * @return
     * @throws Exception
     */
    public boolean delete(T t, String routing) throws Exception;

    /**
     * 根据条件删除索引
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-delete-by-query.html#java-rest-high-document-delete-by-query-response
     *
     * @param queryBuilder 查询条件（官方）
     * @param clazz        索引实体类类型
     * @return
     * @throws Exception
     */
    public BulkByScrollResponse deleteByCondition(QueryBuilder queryBuilder, Class<T> clazz) throws Exception;

    /**
     * 删除索引
     *
     * @param id    索引主键
     * @param clazz 索引实体类类型
     * @return
     * @throws Exception
     */
    public boolean deleteById(M id, Class<T> clazz) throws Exception;

    /**
     * id数据是否存在
     *
     * @param id    索引数据id值
     * @param clazz 索引实体类类型
     * @return
     */
    public boolean exists(M id, Class<T> clazz) throws Exception;
}
