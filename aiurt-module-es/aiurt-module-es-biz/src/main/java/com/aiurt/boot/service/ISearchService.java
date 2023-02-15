package com.aiurt.boot.service;

import com.aiurt.modules.search.dto.SearchRequestDTO;
import com.aiurt.modules.search.dto.SearchResponseDTO;
import com.aiurt.modules.search.dto.TermResponseDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/159:11
 */
public interface ISearchService {

    /**
     * 分页查询故障知识库
     * @param searchRequest
     * @return
     */
    IPage<SearchResponseDTO> faultKnowledgeList(SearchRequestDTO searchRequest);

    /**
     * 词语补全提示
     * @param searchKey
     * @return
     */
    List<TermResponseDTO> suggest(String searchKey);
}
