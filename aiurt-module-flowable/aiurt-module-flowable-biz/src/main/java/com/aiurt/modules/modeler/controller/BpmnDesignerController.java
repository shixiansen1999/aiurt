package com.aiurt.modules.modeler.controller;


import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.modeler.dto.ModelInfoVo;
import com.aiurt.modules.modeler.service.IFlowableBpmnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

/**
 * @Description: flowable bpmn设计器的接口
 * @Author: aiurt
 * @Date:   2022-07-12
 * @Version: V1.0
 */
@Api(tags="bpmn设计器的接口")
@RestController
@RequestMapping("/modeler/designer")
@Slf4j
public class BpmnDesignerController {

    @Autowired
    private IFlowableBpmnService flowableBpmnService;

    /**
     * 根据modelid 查询bpmn xml, 可以预览图片 (修改或者编辑中使用)
     * @param modelId
     * @return
     */
    @GetMapping(value = "/getBpmnByModelId/{modelId}")
    @ApiOperation("根据modelid 查询bpmn xml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "流程模板id", required = true, paramType = "path")
    })
    public Result<ModelInfoVo> getBpmnByModelId(@PathVariable String modelId) {
        if (StrUtil.isNullOrUndefined(modelId)) {
            throw new AiurtBootException("请求参数有误!");
        }
        ModelInfoVo modelInfoVo = flowableBpmnService.loadBpmnXmlByModelId(modelId);
        return Result.ok(modelInfoVo);
    }

    /**
     * 根据流程标识 查询bpmn xml, 可以预览图片 (修改或者编辑中使用)
     * @param modelKey
     * @return
     */
    @GetMapping(value = "/getBpmnByModelId/{modelId}")
    @ApiOperation("根据流程标识 查询bpmn xml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelKey", value = "流程标识", required = true, paramType = "path")
    })
    public Result<ModelInfoVo> getBpmnByModelKey(@PathVariable String modelKey) {
        if (StrUtil.isNullOrUndefined(modelKey)) {
            throw new AiurtBootException("请求参数有误!");
        }
        ModelInfoVo modelInfoVo = flowableBpmnService.loadBpmnXmlByModelKey(modelKey);
        return Result.ok(modelInfoVo);
    }


    /**
     * 保存bpmn模型
     * @param modelInfoVo
     * @return
     */
    @PostMapping(value = "/saveBpmnModel", produces = "application/json")
    @ApiOperation("保存bpmn模型")
    public Result<String> saveBpmnModel(@RequestBody ModelInfoVo modelInfoVo) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(modelInfoVo.getModelXml().getBytes());
        String data = flowableBpmnService.importBpmnModel(modelInfoVo.getModelId(),
                modelInfoVo.getFileName(), byteArrayInputStream, user);
        return Result.OK(data);
    }


    /**
     * 发布Bpmn
     *
     * @param modelId 模型id
     * @return
     */
    @PutMapping(value = "/publishBpmn/{modelId}", produces = "application/json")
    @AutoLog(value = "部署流程")
    @ApiOperation("部署流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "流程模板id", required = true, paramType = "query")
    })
    public Result<?> publishBpmn(@PathVariable String modelId) {

        flowableBpmnService.publishBpmn(modelId);
        return Result.OK("部署成功");
    }

}
