package com.aiurt.boot.knowledge.controller;

import com.aiurt.boot.knowledge.service.KnowledgeBaseSearchService;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseReqDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseResDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author
 * @description 故障知识库高级搜索
 */
@Slf4j
@Api(tags = "故障知识库高级搜索")
@RestController
@RequestMapping("/search")
public class KnowledgeBaseSearchController {

    @Autowired
    private KnowledgeBaseSearchService knowledgeBaseSearchService;

    /**
     * 分页列表查询
     */
    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<KnowledgeBaseResDTO>> search(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     KnowledgeBaseReqDTO knowledgeBaseReqDTO,
                                                     HttpServletRequest req) {
        Page<KnowledgeBaseResDTO> page = new Page<>(pageNo, pageSize);
        IPage<KnowledgeBaseResDTO> pageList = knowledgeBaseSearchService.search(page, knowledgeBaseReqDTO);
        return Result.OK(pageList);
    }

}
