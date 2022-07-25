package com.aiurt.modules.flow.controller;

import com.aiurt.common.aspect.annotation.DisableDataFilter;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.flow.service.FlowApiService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 根据指定流程的主版本，发起一个流程实例。
     *
     * @param processDefinitionKey 流程标识。
     * @return 应答结果对象。
     */
    @PostMapping("/startOnly")
    public Result<Void> startOnly(@RequestBody(required = true) String processDefinitionKey) {
        // 1. 验证流程数据的合法性。
//        ResponseResult<FlowEntry> flowEntryResult = flowOperationHelper.verifyAndGetFlowEntry(processDefinitionKey);
//        if (!flowEntryResult.isSuccess()) {
//            return ResponseResult.errorFrom(flowEntryResult);
//        }
//        // 2. 验证流程一个用户任务的合法性。
//        FlowEntryPublish flowEntryPublish = flowEntryResult.getData().getMainFlowEntryPublish();
//        ResponseResult<TaskInfoVo> taskInfoResult =
//                flowOperationHelper.verifyAndGetInitialTaskInfo(flowEntryPublish, false);
//        if (!taskInfoResult.isSuccess()) {
//            return ResponseResult.errorFrom(taskInfoResult);
//        }
//        flowApiService.start(flowEntryPublish.getProcessDefinitionId(), null);
        return Result.OK();
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
}
