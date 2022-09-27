package com.aiurt.modules.robot.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.robot.dto.TaskPathInfoDTO;
import com.aiurt.modules.robot.entity.TaskPathInfo;
import com.aiurt.modules.robot.service.ITaskPathInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
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
}
