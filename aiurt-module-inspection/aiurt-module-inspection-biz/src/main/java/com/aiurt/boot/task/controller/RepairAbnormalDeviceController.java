package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.dto.RepairAbnormalDeviceAddDTO;
import com.aiurt.boot.task.entity.RepairAbnormalDevice;
import com.aiurt.boot.task.service.IRepairAbnormalDeviceService;
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
 * @since 2023/10/19
 */
@Api(tags = "检修结果异常设备")
@RestController
@RequestMapping("/repairDevice")
@Slf4j
public class RepairAbnormalDeviceController extends BaseController<RepairAbnormalDevice, IRepairAbnormalDeviceService> {

    @Autowired
    private IRepairAbnormalDeviceService repairAbnormalDeviceService;

    @AutoLog(value = "保存异常设备")
    @ApiOperation(value = "保存异常设备", notes = "保存异常设备")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody RepairAbnormalDeviceAddDTO repairAbnormalDeviceAddDTO) {
        repairAbnormalDeviceService.add(repairAbnormalDeviceAddDTO);
        return Result.ok("保存成功");
    }
}
