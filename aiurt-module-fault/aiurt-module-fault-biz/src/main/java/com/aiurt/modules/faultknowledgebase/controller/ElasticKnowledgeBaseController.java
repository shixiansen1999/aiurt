package com.aiurt.modules.faultknowledgebase.controller;

import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseMatchDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseReqDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseResDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 故障知识库高级搜索
 * @Author: aiurt
 * @Version: V1.0
 */
@Api(tags = "故障知识库高级搜索")
@RestController
@RequestMapping("/search")
@Slf4j
public class ElasticKnowledgeBaseController extends BaseController<FaultKnowledgeBase, IFaultKnowledgeBaseService> {

    @Autowired
    private IFaultKnowledgeBaseService faultKnowledgeBaseService;

    /**
     * 分页列表查询
     */
    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "fault/FaultKnowledgeBaseListChange")
    public Result<IPage<KnowledgeBaseResDTO>> search(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     KnowledgeBaseReqDTO knowledgeBaseReqDTO,
                                                     HttpServletRequest request) {
        Page<KnowledgeBaseResDTO> page = new Page<>(pageNo, pageSize);
        IPage<KnowledgeBaseResDTO> pageList = faultKnowledgeBaseService.search(page, request, knowledgeBaseReqDTO);
        return Result.OK(pageList);
    }

    /**
     * 知识库数据同步至ES
     */
    @ApiOperation(value = "知识库数据同步至ES", notes = "知识库数据同步至ES")
    @PostMapping(value = "/synchrodata")
    public Result<?> synchrodata(HttpServletRequest request, HttpServletResponse response) {
        faultKnowledgeBaseService.synchrodata(request, response);
        return Result.OK("知识库数据同步完成！");
    }

    /**
     * 智能助手知识库数据匹配
     */
    @ApiOperation(value = "智能助手知识库数据匹配", notes = "智能助手知识库数据匹配")
    @RequestMapping(value = "/matching", method = RequestMethod.GET)
    public Result<IPage<KnowledgeBaseResDTO>> knowledgeBaseMatching(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                    KnowledgeBaseMatchDTO knowledgeBaseMatchDTO) {
        Page<KnowledgeBaseResDTO> page = new Page<>(pageNo, pageSize);
        IPage<KnowledgeBaseResDTO> pageList = faultKnowledgeBaseService.knowledgeBaseMatching(page, knowledgeBaseMatchDTO);
        return Result.OK(pageList);
    }

    /**
     * 智能助手故障现象匹配
     *
     * @param request
     * @param response
     * @param knowledgeBaseMatchDTO
     * @return
     */
    @ApiOperation(value = "智能助手故障现象匹配", notes = "智能助手故障现象匹配")
    @RequestMapping(value = "/phenomenon", method = RequestMethod.GET)
    public Result<List<String>> phenomenonMatching(HttpServletRequest request, HttpServletResponse response,
                                                   KnowledgeBaseMatchDTO knowledgeBaseMatchDTO) {
        List<String> list = faultKnowledgeBaseService.phenomenonMatching(request, response, knowledgeBaseMatchDTO);
        return Result.OK(list);
    }
}
