package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.dto.PatrolAbnormalDeviceAddDTO;
import com.aiurt.boot.task.entity.PatrolAbnormalDevice;
import com.aiurt.boot.task.service.IPatrolAbnormalDeviceService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sbx
 * @since 2023/10/17
 */
@Api(tags = "巡检结果异常设备")
@RestController
@RequestMapping("/patrolAbnormalDevice")
@Slf4j
public class PatrolAbnormalDeviceController extends BaseController<PatrolAbnormalDevice, IPatrolAbnormalDeviceService> {

    @Autowired
    private IPatrolAbnormalDeviceService patrolAbnormalDeviceService;

    @AutoLog(value = "保存异常设备")
    @ApiOperation(value = "保存异常设备", notes = "保存异常设备")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody PatrolAbnormalDeviceAddDTO patrolAbnormalDeviceAddDTO) {
        patrolAbnormalDeviceService.add(patrolAbnormalDeviceAddDTO);
        return Result.ok("保存成功");
    }
}
