package com.aiurt.modules.flow.controller;

import com.aiurt.common.aspect.annotation.DisableDataFilter;
import com.aiurt.modules.flow.dto.*;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.flow.service.IActCustomTaskCommentService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;

/**
 * 流程操作接口类
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Api(tags = "通用流程操作接口")
@Slf4j
@RestController
@RequestMapping("/flow/flowOperation")
public class FlowOperationController {


    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private IActCustomTaskCommentService actCustomTaskCommentService;

    /**
     * 根据指定流程的主版本，启动一个流程实例，同时作为第一个任务节点的执行人，执行第一个用户任务。
     * 该接口无需数据权限过滤，因此用DisableDataFilter注解标注。如果当前系统没有支持数据权限过滤，该注解不会有任何影响。
     *
     * @param startBpmnDTO
     * @return 应答结果对象。
     */
    @DisableDataFilter
    @PostMapping("/startAndTakeUserTask")
    public Result<?> startAndTakeUserTask(@RequestBody StartBpmnDTO startBpmnDTO) {
        ProcessInstance processInstance = flowApiService.startAndTakeFirst(startBpmnDTO);
        return Result.OK(processInstance);
    }

    /**
     * 获取开始节点之后的第一个任务节点的数据。
     *
     * @param processDefinitionKey 流程标识。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewInitialTaskInfo")
    public Result<TaskInfoDTO> viewInitialTaskInfo(@RequestParam String processDefinitionKey) {
//        ResponseResult<FlowEntry> flowEntryResult = flowOperationHelper.verifyAndGetFlowEntry(processDefinitionKey);
//        if (!flowEntryResult.isSuccess()) {
//            return ResponseResult.errorFrom(flowEntryResult);
//        }
//        FlowEntryPublish flowEntryPublish = flowEntryResult.getData().getMainFlowEntryPublish();
//        String initTaskInfo = flowEntryPublish.getInitTaskInfo();
//        TaskInfoVo taskInfo = StrUtil.isBlank(initTaskInfo)
//                ? null : JSON.parseObject(initTaskInfo, TaskInfoVo.class);
//        if (taskInfo != null) {
//            String loginName = TokenData.takeFromRequest().getLoginName();
//            taskInfo.setAssignedMe(StrUtil.equalsAny(
//                    taskInfo.getAssignee(), loginName, FlowConstant.START_USER_NAME_VAR));
//        }
        return Result.OK(new TaskInfoDTO());
    }

    /**
     * 提交流程的用户任务
     *
     * @return
     */
    @DisableDataFilter
    @PostMapping("/submitUserTask")
    public Result<?> completeTask() {
        return Result.OK();
    }

    /**
     * 我的待办
     *
     * @param flowTaskReqDTO
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "我的待办", notes = "我的待办")
    @GetMapping(value = "/listRuntimeTask")
    public Result<IPage<FlowTaskDTO>> listRuntimeTask(@RequestBody FlowTaskReqDTO flowTaskReqDTO,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<FlowTaskDTO> pageList = flowApiService.listRuntimeTask(pageNo, pageSize, flowTaskReqDTO);
        return Result.OK(pageList);
    }

    /**
     * 获取指定流程定义的流程图。
     *
     * @param processDefinitionId 流程定义Id。
     * @return 流程图。
     */
    @GetMapping("/viewProcessBpmn")
    public Result<String> viewProcessBpmn(@RequestParam String processDefinitionId) throws IOException {
        BpmnXMLConverter converter = new BpmnXMLConverter();
        BpmnModel bpmnModel = flowApiService.getBpmnModelByDefinitionId(processDefinitionId);
        byte[] xmlBytes = converter.convertToXML(bpmnModel);
        InputStream in = new ByteArrayInputStream(xmlBytes);
        return Result.OK("获取成功", StreamUtils.copyToString(in, StandardCharsets.UTF_8));
    }


    /**
     * 获取流程图高亮数据。
     *
     * @param processInstanceId 流程实例Id。
     * @return 流程图高亮数据。
     */
    @GetMapping("/viewHighlightFlowData")
    public Result<JSONObject> viewHighlightFlowData(@RequestParam String processInstanceId) {
        JSONObject jsonData = flowApiService.viewHighlightFlowData(processInstanceId);
        return Result.OK(jsonData);
    }

    /**
     * 获取当前流程任务的审批列表。
     *
     * @param processInstanceId 当前运行时的流程实例Id。
     * @return 当前流程实例的详情数据。
     */
    @GetMapping("/listFlowTaskComment")
    public Result<List<FlowTaskCommentDTO>> listFlowTaskComment(@RequestParam String processInstanceId) {
        List<ActCustomTaskComment> actCustomTaskComments =
                actCustomTaskCommentService.getFlowTaskCommentList(processInstanceId);
        List<FlowTaskCommentDTO> resultList = actCustomTaskCommentService.convertToCustomTaskCommentList(actCustomTaskComments);
        return Result.OK(resultList);
    }


    /**
     * 获取流程运行时指定任务的信息。
     *
     * @param processDefinitionId 流程引擎的定义Id。
     * @param processInstanceId   流程引擎的实例Id。
     * @param taskId              流程引擎的任务Id。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewRuntimeTaskInfo")
    public Result<TaskInfoDTO> viewRuntimeTaskInfo(
            @RequestParam String processDefinitionId,
            @RequestParam String processInstanceId,
            @RequestParam String taskId) {
        TaskInfoDTO taskInfoVo = flowApiService.viewRuntimeTaskInfo(processDefinitionId, processInstanceId, taskId);
        return Result.OK(taskInfoVo);
    }

    /**
     * 已办任务
     *
     * @param processDefinitionName 流程名。
     * @param beginDate             流程发起开始时间。
     * @param endDate               流程发起结束时间。
     * @param pageNo                当前页。
     * @param pageSize              每页数量。
     * @return 查询结果应答。
     */
    @PostMapping("/listHistoricTask")
    public Result<IPage<FlowHisTaskDTO>> listHistoricTask(
            @RequestParam String processDefinitionName,
            @RequestParam String beginDate,
            @RequestParam String endDate,
            @RequestParam(name = "pageNo", defaultValue = "1") @ApiParam(required = true) Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(required = true) Integer pageSize) throws ParseException {
        IPage<FlowHisTaskDTO> pageList =flowApiService.listHistoricTask(processDefinitionName,beginDate,endDate,pageNo,pageSize);
        return Result.OK(pageList);
    }

    /**
     * 流程实例
     * @return
     */
    @PostMapping("/listAllHistoricProcessInstance")
    @ApiOperation("流程实例")
    public Result<IPage<HistoricProcessInstanceDTO>> listAllHistoricProcessInstance() {
        return Result.OK();
    }

    /**
     * 历史任务查询
     * @return
     */
    @ApiOperation("历史任务查询")
    @PostMapping("listHistoricProcessInstance")
    public Result<?> listHistoricProcessInstance() {
        return Result.OK();
    }


}
