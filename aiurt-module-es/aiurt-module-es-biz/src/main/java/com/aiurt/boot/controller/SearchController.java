package com.aiurt.boot.controller;


import com.aiurt.boot.service.ISearchService;
import com.aiurt.modules.search.dto.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/148:37
 */
@Api(tags = "搜索服务")
@RestController
@RequestMapping("/search/")
@Slf4j
public class SearchController {
    @Autowired
    private ISearchService searchService;

    @ApiOperation(value = "分页查询故障知识库", notes = "分页查询故障知识库")
    @GetMapping(value = "/faultKnowledgeList")
    public Result<IPage<SearchResponseDTO>> faultKnowledgeList(SearchRequestDTO searchRequest) {
        IPage<SearchResponseDTO> result = searchService.faultKnowledgeList(searchRequest);
        return Result.OK(result);
    }

    @ApiOperation(value = "故障知识库词语补全提示", notes = "故障知识库词语补全提示")
    @GetMapping(value = "/faultKnowledgeSuggest")
    public Result<List<String>> faultKnowledgeSuggest(String keyword) {
        List<String> result = searchService.faultKnowledgeSuggest(keyword);
        return Result.OK(result);
    }

    @ApiOperation(value = "分页查询规程规范与知识库", notes = "分页查询规程规范与知识库")
    @GetMapping(value = "/documentManageList")
    public Result<IPage<DocumentManageResponseDTO>> documentManageList(DocumentManageRequestDTO documentManageRequest) {
        IPage<DocumentManageResponseDTO> result = searchService.documentManageList(documentManageRequest);
        return Result.OK(result);
    }

    @ApiOperation(value = "规程规范与知识库词语补全提示", notes = "规程规范与知识库词语补全提示")
    @GetMapping(value = "/documentManageSuggest")
    public Result<List<String>> documentManageSuggest(String keyword) {
        List<String> result = searchService.documentManageSuggest(keyword);
        return Result.OK(result);
    }

}
