package com.aiurt.modules.modeler.controller;


import com.aiurt.modules.modeler.dto.ModelInfoVo;
import com.aiurt.modules.modeler.service.IFlowableBpmnService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
     * 根据modelid 查询bpmn xml
     * @param modelId
     * @return
     */
    @GetMapping(value = "/getBpmnByModelId/{modelId}")
    public Result<ModelInfoVo> getBpmnByModelId(@PathVariable String modelId) {
        ModelInfoVo modelInfoVo = flowableBpmnService.loadBpmnXmlByModelId(modelId);
        return Result.ok(modelInfoVo);
    }


    /**
     * 保存bpmn模型
     * @param modelInfoVo
     * @return
     */
    @PostMapping(value = "/saveBpmnModel", produces = "application/json")
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
    @PostMapping(value = "/publishBpmn/{modelId}", produces = "application/json")
    public Result<?> publishBpmn(@PathVariable String modelId) {
        flowableBpmnService.publishBpmn(modelId);
        return Result.OK("部署成功");
    }

}
