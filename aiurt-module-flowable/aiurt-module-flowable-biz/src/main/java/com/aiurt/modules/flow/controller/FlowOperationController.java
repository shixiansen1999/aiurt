package com.aiurt.modules.flow.controller;

import com.aiurt.common.aspect.annotation.DisableDataFilter;
import com.aiurt.modules.flow.dto.FlowTaskDTO;
import com.aiurt.modules.flow.dto.FlowTaskReqDTO;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.flow.service.FlowApiService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
     * 待办任务
     *
     * @return
     */
    @ApiOperation(value = "待办任务", notes = "待办任务")
    @GetMapping(value = "/listRuntimeTask")
    public Result<IPage<FlowTaskDTO>> listRuntimeTask(@RequestBody FlowTaskReqDTO flowTaskReqDTO,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        IPage<FlowTaskDTO> pageList = flowApiService.listRuntimeTask(pageNo, pageSize, flowTaskReqDTO);
        return Result.OK(pageList);
    }
}
