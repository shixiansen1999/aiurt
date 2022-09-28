package com.aiurt.modules.robot.controller;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.robot.entity.TaskExcuteData;
import com.aiurt.modules.robot.service.ITaskExcuteDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: task_excute_data
 * @Author: aiurt
 * @Date: 2022-09-28
 * @Version: V1.0
 */
@Api(tags = "机器人当前执行任务")
@RestController
@RequestMapping("/robot/taskExcuteData")
@Slf4j
public class TaskExcuteDataController extends BaseController<TaskExcuteData, ITaskExcuteDataService> {
    @Autowired
    private ITaskExcuteDataService taskExcuteDataService;

    /**
     * 当前机器人执行的任务
     *
     * @param robotIp 机器人ip
     * @return
     */
    @AutoLog(value = "当前机器人执行的任务")
    @ApiOperation(value = "当前机器人执行的任务", notes = "当前机器人执行的任务")
    @GetMapping(value = "/getTaskExcuteData")
    @ApiImplicitParam(name = "robotIp", value = "机器人ip", required = true, example = "192.168.1.10", dataTypeClass = String.class)
    public Result<TaskExcuteData> getTaskExcuteData(@RequestParam(name = "robotIp") String robotIp) {
        TaskExcuteData result = taskExcuteDataService.getTaskExcuteData(robotIp);
        return Result.OK(result);
    }


    /**
     * 同步机器人当前执行任务信息
     *
     * @param robotId 机器人id
     * @return
     */
    @AutoLog(value = "同步机器人当前执行任务信息")
    @ApiOperation(value = "同步机器人当前执行任务信息", notes = "同步机器人当前执行任务信息")
    @GetMapping(value = "/synchronizeTaskExcuteData")
    @ApiImplicitParam(name = "robotId", value = "机器人id", required = false, example = "1542055710727028737", dataTypeClass = String.class)
    public Result<?> synchronizeTaskExcuteData(@RequestParam(name = "robotId", required = false) String robotId) {
        taskExcuteDataService.synchronizeTaskExcuteData(CollUtil.newArrayList(robotId));
        return Result.OK("同步机器人当前执行任务信息成功");
    }


}
