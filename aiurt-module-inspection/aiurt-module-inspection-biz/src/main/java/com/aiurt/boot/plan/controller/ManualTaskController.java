package com.aiurt.boot.plan.controller;

import com.aiurt.boot.plan.dto.RepairDeviceDTO;
import com.aiurt.boot.plan.dto.RepairPoolDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.req.ManualTaskReq;
import com.aiurt.boot.plan.req.RepairPoolReq;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.enums.ModuleType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author wgp
 * @Title:
 * @Description: 手工下发任务
 * @date 2022/6/289:35
 */
@Api(tags = "手工下发任务")
@RestController
@RequestMapping("/plan/manualTask")
@Slf4j
public class ManualTaskController {
    @Resource
    private IRepairPoolService repairPoolService;

    /**
     * 分页查询手工下发任务列表
     *
     * @param manualTaskReq
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "检修管理-手工下发任务-列表查询", operateType =  1, operateTypeAlias = "列表查询", permissionUrl = "/views/overhaul/RepairManualPoolList")
    @ApiOperation(value = "分页查询手工下发任务列表", notes = "分页查询手工下发任务列表")
    @PermissionData(pageComponent = "overhaul/RepairManualPoolList")
    @GetMapping(value = "/listPage")
    public Result<IPage<RepairPool>> listPage(ManualTaskReq manualTaskReq,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<RepairPool> page = new Page<RepairPool>(pageNo, pageSize);
        IPage<RepairPool> pageList = repairPoolService.listPage(page, manualTaskReq);
        return Result.OK(pageList);
    }

    /**
     * 添加手工下发检修任务
     *
     * @param repairPoolReq
     * @return
     */
    @AutoLog(value = "检修管理-手工下发任务-添加手工下发任务", operateType =  2, operateTypeAlias = "添加手工下发任务", module = ModuleType.INSPECTION)
    @ApiOperation(value = "添加手工下发检修任务", notes = "添加手工下发检修任务")
    @PostMapping(value = "/addManualTask")
    public Result<String> addManualTask(@RequestBody RepairPoolReq repairPoolReq) {
        repairPoolService.addManualTask(repairPoolReq);
        return Result.OK("添加成功！");
    }

    /**
     * 通过id查询手工下发检修任务信息
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修管理-手工下发任务-编辑手工下发任务", operateType =  1, operateTypeAlias = "编辑手工下发任务", module = ModuleType.INSPECTION)
    @ApiOperation(value = "通过id查询手工下发检修任务信息", notes = "通过id查询手工下发检修任务信息")
    @GetMapping(value = "/queryManualTaskById")
    public Result<RepairPoolDTO> queryManualTaskById(@RequestParam(name = "id", required = true) String id) {
        RepairPoolDTO repairPoolDTO = repairPoolService.queryManualTaskById(id);
        return Result.OK(repairPoolDTO);
    }

    /**
     * 修改手工下发检修任务信息
     *
     * @param repairPoolReq
     * @return
     */
    @AutoLog(value = "检修管理-手工下发任务-修改手工下发任务", operateType =  3, operateTypeAlias = "修改手工下发任务", module = ModuleType.INSPECTION)
    @ApiOperation(value = "修改手工下发检修任务信息", notes = "编辑手工下发检修任务信息")
    @PostMapping(value = "/updateManualTaskById")
    public Result<String> updateManualTaskById(@RequestBody RepairPoolReq repairPoolReq) {
        repairPoolService.updateManualTaskById(repairPoolReq);
        return Result.OK("修改成功!");
    }

    /**
     * 通过id删除手工下发检修任务
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修管理-手工下发任务-删除手工下发任务", operateType =  4, operateTypeAlias = "删除手工下发任务", module = ModuleType.INSPECTION)
    @ApiOperation(value = "通过id删除手工下发检修任务", notes = "通过id删除手工下发检修任务")
    @DeleteMapping(value = "/deleteManualTaskById")
    public Result<String> deleteManualTaskById(@RequestParam @ApiParam(name = "id", required = true, value = "任务id") String id) {
        repairPoolService.deleteManualTaskById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 根据检修任务code和检修标准id查询检修标准对应的设备
     *
     * @param code 检修任务code
     * @param id   检修标准id
     * @return
     */
    @AutoLog(value = "检修管理-手工下发任务-查询设备", operateType =  1, operateTypeAlias = "查询设备", module = ModuleType.INSPECTION)
    @ApiOperation(value = "根据检修任务code和检修标准id查询检修标准对应的设备", notes = "根据检修任务code和检修标准id查询检修标准对应的设备")
    @GetMapping(value = "/queryDeviceByCodeAndId")
    public Result<IPage<RepairDeviceDTO>> queryDeviceByCodeAndId(@RequestParam @ApiParam(name = "code", required = true, value = "检修任务code") String code,
                                                                     @RequestParam @ApiParam(name = "id", required = true, value = "检修标准id") String id,
                                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<RepairDeviceDTO> page = new Page<>(pageNo, pageSize);
        IPage<RepairDeviceDTO> pageList = repairPoolService.queryDeviceByCodeAndId(page, code, id);
        return Result.OK(pageList);
    }
}
