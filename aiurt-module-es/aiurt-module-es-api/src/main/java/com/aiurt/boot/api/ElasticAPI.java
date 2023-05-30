package com.aiurt.boot.api;

import org.elasticsearch.action.bulk.BulkResponse;

import java.util.List;

public interface ElasticAPI<T> {

    /**
     * 通用批量添加
     *
     * @param list
     * @return
     * @throws Exception
     */
    BulkResponse[] saveBatch(List<T> list) throws Exception;

}
