package com.aiurt.modules.robot.controller;


import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.robot.dto.AreaPointDTO;
import com.aiurt.modules.robot.service.ITaskPointRelService;
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

import java.util.List;

/**
 * @Description: task_point_rel
 * @Author: aiurt
 * @Date: 2022-09-27
 * @Version: V1.0
 */
@Api(tags = "任务模板与点位关联")
@RestController
@RequestMapping("/robot/taskPointRel")
@Slf4j
public class TaskPointRelController  {
    @Autowired
    private ITaskPointRelService taskPointRelService;


    /**
     * 通过任务模板id查询巡检点位
     *
     * @param taskPathId 任务模板id
     * @return
     */
    @AutoLog(value = "通过任务模板id查询巡检点位")
    @ApiOperation(value = "通过任务模板id查询巡检点位", notes = "通过任务模板id查询巡检点位")
    @GetMapping(value = "/queryPointByTaskPathId")
    @ApiImplicitParam(name = "taskPathId", value = "任务模板id", required = true, example = "37c01ba77569a6ec6", dataTypeClass = String.class)
    public Result<List<AreaPointDTO>> queryPointByTaskPathId(@RequestParam(name = "taskPathId", required = true) String taskPathId) {
        List<AreaPointDTO> result = taskPointRelService.queryPointByTaskPathId(taskPathId);
        return Result.OK(result);
    }


}
