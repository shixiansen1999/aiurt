package com.aiurt.modules.robot.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.robot.constant.RobotConstant;
import com.aiurt.modules.robot.dto.TaskPathInfoDTO;
import com.aiurt.modules.robot.entity.TaskPathInfo;
import com.aiurt.modules.robot.service.ITaskPathInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description: task_path_info
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
@Api(tags = "机器人任务模板")
@RestController
@RequestMapping("/robot/taskPathInfo")
@Slf4j
public class TaskPathInfoController extends BaseController<TaskPathInfo, ITaskPathInfoService> {
    @Resource
    private ITaskPathInfoService taskPathInfoService;

    /**
     * 任务模板列表分页查询
     *
     * @param taskPathInfo
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "任务模板列表分页查询")
    @ApiOperation(value = "任务模板列表分页查询", notes = "任务模板列表分页查询")
    @GetMapping(value = "/list")
    public Result<IPage<TaskPathInfoDTO>> queryPageList(TaskPathInfoDTO taskPathInfo,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        Page<TaskPathInfoDTO> page = new Page<>(pageNo, pageSize);
        IPage<TaskPathInfoDTO> pageList = taskPathInfoService.queryPageList(page, taskPathInfo);
        return Result.OK(pageList);
    }

    /**
     * 同步机器人任务模板
     *
     * @return
     */
    @AutoLog(value = "同步机器人任务模板")
    @ApiOperation(value = "同步机器人任务模板", notes = "同步机器人任务模板")
    @GetMapping(value = "/synchronizeTaskPathInfo")
    public Result<String> synchronizeTaskPathInfo() {
        taskPathInfoService.synchronizeTaskPathInfo();
        return Result.OK("同步机器人任务模板成功");
    }

    /**
     * 立即执行
     *
     * @param taskPathId 任务模板id
     * @return
     */
    @AutoLog(value = "立即执行")
    @ApiOperation(value = "立即执行", notes = "根据任务模板id给机器人发任务")
    @GetMapping(value = "/startTaskByPathId")
    @ApiImplicitParam(name = "taskPathId", value = "任务模板id", required = true, example = "37c01ba77569a6ec6", dataTypeClass = String.class)
    public Result<?> startTaskByPathId(@RequestParam(name = "taskPathId") String taskPathId) {
        int result = taskPathInfoService.startTaskByPathId(taskPathId);
        return result == RobotConstant.RESULT_SUCCESS_0 ? Result.OK("任务发送成功") : Result.error("任务发送失败");
    }


}
