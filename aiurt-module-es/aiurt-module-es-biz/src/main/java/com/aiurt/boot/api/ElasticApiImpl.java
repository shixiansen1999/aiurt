package com.aiurt.boot.api;

import com.aiurt.boot.core.service.ElasticService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ElasticApiImpl<T> implements ElasticAPI {

    @Autowired
    private ElasticService elasticService;

    @Override
    public BulkResponse[] saveBatch(List list) throws Exception {
        return elasticService.saveBatch(list);
    }
}
