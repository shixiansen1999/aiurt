package com.aiurt.boot.api;

import com.aiurt.modules.knowledge.dto.KnowledgeBaseReqDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseResDTO;
import com.aiurt.modules.knowledge.entity.KnowledgeBase;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.elasticsearch.action.bulk.BulkResponse;

import java.io.IOException;
import java.util.List;

public interface ElasticAPI {

    /**
     * 知识库数据批量添加
     *
     * @param list
     * @return
     * @throws Exception
     */
    BulkResponse[] saveBatch(List<KnowledgeBase> list) throws Exception;

    /**
     * 知识库高级搜索-分页查询列表
     *
     * @param page
     * @param knowledgeBaseReqDTO
     * @return
     */
    IPage<KnowledgeBaseResDTO> search(Page<KnowledgeBaseResDTO> page, KnowledgeBaseReqDTO knowledgeBaseReqDTO) throws IOException;

}
