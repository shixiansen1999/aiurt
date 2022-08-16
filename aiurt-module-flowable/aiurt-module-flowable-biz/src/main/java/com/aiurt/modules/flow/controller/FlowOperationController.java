package com.aiurt.modules.flow.controller;

import com.aiurt.common.aspect.annotation.DisableDataFilter;
import com.aiurt.modules.flow.dto.*;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.flow.service.IActCustomTaskCommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public Result<HighLightedNodeDTO> viewHighlightFlowData(@RequestParam String processInstanceId) {
        HighLightedNodeDTO jsonData = flowApiService.viewHighlightFlowData(processInstanceId);
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
        IPage<FlowHisTaskDTO> pageList = flowApiService.listHistoricTask(processDefinitionName, beginDate, endDate, pageNo, pageSize);
        return Result.OK(pageList);
    }

    /**
     * 流程实例
     * <p>
     * 流程实例, 所有历史流程数据。
     *
     * @return
     */
    @PostMapping("/listAllHistoricProcessInstance")
    @ApiOperation("流程实例")
    public Result<IPage<HistoricProcessInstanceDTO>> listAllHistoricProcessInstance(@RequestBody HistoricProcessInstanceReqDTO reqDTO) {
        IPage<HistoricProcessInstanceDTO> result = flowApiService.listAllHistoricProcessInstance(reqDTO);
        return Result.OK(result);
    }

    /**
     * 根据输入参数查询，当前用户的历史流程数据。
     * 历史任务查询
     *
     * @return
     */
    @ApiOperation("历史任务查询")
    @PostMapping("listHistoricProcessInstance")
    public Result<IPage<HistoricProcessInstanceDTO>> listHistoricProcessInstance(@RequestBody HistoricProcessInstanceReqDTO reqDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        reqDTO.setLoginName(loginUser.getUsername());
        IPage<HistoricProcessInstanceDTO> result = flowApiService.listAllHistoricProcessInstance(reqDTO);
        return Result.OK(result);
    }

    /**
     * 转办任务
     *
     * @param params 参数
     * @return
     */
    @PostMapping(value = "/turnTask")
    public Result<?> turnTask(TurnTaskDTO params) {
        flowApiService.turnTask(params);
        return Result.OK("转办成功");
    }

    /**
     * 获取可驳回节点列表
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @GetMapping(value = "/getBackNodesByProcessInstanceId/{processInstanceId}/{taskId}")
    public Result<List<FlowNodeDTO>> getBackNodesByProcessInstanceId(@PathVariable String processInstanceId, @PathVariable String taskId) {
        List<FlowNodeDTO> datas = flowApiService.getBackNodesByProcessInstanceId(processInstanceId, taskId);
        return Result.OK(datas);
    }

    /**
     * 终止流程
     *
     * @param instanceDTO
     * @return
     */
    @ApiOperation("终止流程")
    @PutMapping("stopProcessInstance")
    public Result<?> stopProcessInstance(@Valid @RequestBody StopProcessInstanceDTO instanceDTO) {
        flowApiService.stopProcessInstance(instanceDTO);
        return Result.OK("终止流程成功");
    }

    /**
     * 删除流程
     *
     * @return
     */
    @DeleteMapping("/deleteProcessInstance")
    @ApiOperation("删除流程")
    public Result<?> deleteProcessInstance(@RequestParam(value = "processInstanceId") String processInstanceId) {
        flowApiService.deleteProcessInstance(processInstanceId);
        return Result.OK("终止流程成功");
    }

    /**
     * 主动驳回当前的待办任务，只用当前待办任务的指派人或者候选者才能完成该操作。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            待办任务Id。
     * @param targetKey         驳回到哪一步的任务标识。
     * @param comment           驳回备注。
     * @return
     */
    @PostMapping("/rejectRuntimeTask")
    public Result<Void> rejectRuntimeTask(
            @RequestParam String processInstanceId,
            @RequestParam String taskId,
            @RequestParam(required = false) String targetKey,
            @RequestParam String comment) {
        Task task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
        flowApiService.backToRuntimeTask(task, targetKey, true, comment);
        return Result.OK();
    }

    /**
     * 任务签收
     * @return
     */
    @PostMapping("/claimTask")
    public Result<?> claimTask(@RequestBody ClaimTaskDTO claimTaskDTO){
        return Result.ok();
    }

    /**
     * 根据业务id 获取历史记录
     * @return
     */
    @GetMapping("/getHistoricLog")
    public Result<?> getHistoricLog(@RequestParam(value = "businessKey") String businessKey) {
        return null;
    }
}
