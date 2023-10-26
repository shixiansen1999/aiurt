package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.dto.PatrolDeviceDTO;
import com.aiurt.boot.task.entity.PatrolDevice;
import com.aiurt.boot.task.service.IPatrolDeviceService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sbx
 * @since 2023/10/18
 */
@Api(tags = "巡视任务关联设备")
@RestController
@RequestMapping("/patrolDevice")
@Slf4j
public class PatrolDeviceController extends BaseController<PatrolDevice, IPatrolDeviceService> {

    @Autowired
    private IPatrolDeviceService patrolDeviceService;

    @AutoLog(value = "通过任务id和巡检任务标准关联表id查询任务标准关联设备")
    @ApiOperation(value = "通过任务id和巡检任务标准关联表id查询任务标准关联设备", notes = "通过任务id和巡检任务标准关联表id查询任务标准关联设备")
    @GetMapping(value = "/devices")
    public Result<List<PatrolDeviceDTO>> queryDevices(@RequestParam @ApiParam(value = "巡视任务id") String taskId,
                                                      @RequestParam @ApiParam(value = "巡视任务标准关联表id") String taskStandardId,
                                                      @RequestParam @ApiParam(value = "巡视工单的设备code") String deviceCode) {
        List<PatrolDeviceDTO> list = patrolDeviceService.queryDevices(taskId, taskStandardId, deviceCode);
        return Result.ok(list);
    }

}
