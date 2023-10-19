package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.dto.RepairDeviceDTO;
import com.aiurt.boot.task.entity.RepairDevice;
import com.aiurt.boot.task.service.IRepairDeviceService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sbx
 * @since 2023/10/19
 */
@Api(tags = "检修任务关联设备")
@RestController
@RequestMapping("/repairDevice")
@Slf4j
public class RepairDeviceController extends BaseController<RepairDevice, IRepairDeviceService> {

    @Autowired
    private IRepairDeviceService repairDeviceService;

    @AutoLog(value = "根据任务id和任务标准关联表id查询设备")
    @ApiOperation(value = "根据任务id和任务标准关联表id查询设备", notes = "根据任务id和任务标准关联表id查询设备")
    @GetMapping(value = "/devices")
    public Result<List<RepairDeviceDTO>> queryDevices(String taskId, String taskStandardId) {
        List<RepairDeviceDTO> list = repairDeviceService.queryDevices(taskId, taskStandardId);
        return Result.ok(list);
    }
}
