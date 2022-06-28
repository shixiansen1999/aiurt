package com.aiurt.boot.plan.controller;

import com.aiurt.boot.plan.dto.RepairPoolDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
     * @param repairPool
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "分页查询手工下发任务列表")
    @ApiOperation(value = "分页查询手工下发任务列表", notes = "分页查询手工下发任务列表")
    @GetMapping(value = "/listPage")
    public Result<IPage<RepairPool>> listPage(RepairPool repairPool,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        QueryWrapper<RepairPool> queryWrapper = QueryGenerator.initQueryWrapper(repairPool, req.getParameterMap());
        Page<RepairPool> page = new Page<RepairPool>(pageNo, pageSize);
        IPage<RepairPool> pageList = repairPoolService.listPage(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加手工下发检修任务
     *
     * @param repairPoolDTO
     * @return
     */
    @AutoLog(value = "添加手工下发检修任务")
    @ApiOperation(value = "添加手工下发检修任务", notes = "添加手工下发检修任务")
    @PostMapping(value = "/addManualTask")
    public Result<String> addManualTask(@RequestBody RepairPoolDTO repairPoolDTO) {
        repairPoolService.addManualTask(repairPoolDTO);
        return Result.OK("添加成功！");
    }

    /**
     * 通过id查询手工下发检修任务信息
     *
     * @param id
     * @return
     */
    @AutoLog(value = "通过id查询手工下发检修任务信息")
    @ApiOperation(value = "通过id查询手工下发检修任务信息", notes = "通过id查询手工下发检修任务信息")
    @GetMapping(value = "/queryManualTaskById")
    public Result<RepairPoolDTO> queryManualTaskById(@RequestParam(name = "id", required = true) String id) {
        RepairPoolDTO repairPoolDTO = repairPoolService.queryManualTaskById(id);
        return Result.OK(repairPoolDTO);
    }

    /**
     * 修改手工下发检修任务信息
     *
     * @param repairPoolDTO
     * @return
     */
    @AutoLog(value = "修改手工下发检修任务信息")
    @ApiOperation(value = "修改手工下发检修任务信息", notes = "修改手工下发检修任务信息")
    @GetMapping(value = "/updateManualTaskById")
    public Result<String> updateManualTaskById(@RequestBody RepairPoolDTO repairPoolDTO) {
        repairPoolService.updateManualTaskById(repairPoolDTO);
        return Result.OK("修改成功!");
    }

    /**
     * 通过id删除手工下发检修任务
     *
     * @param id
     * @return
     */
    @AutoLog(value = "通过id删除手工下发检修任务")
    @ApiOperation(value = "通过id删除手工下发检修任务", notes = "通过id删除手工下发检修任务")
    @DeleteMapping(value = "/deleteManualTaskById")
    public Result<String> deleteManualTaskById(@RequestParam @ApiParam(name = "id", required = true,value = "任务id") String id) {
        repairPoolService.deleteManualTaskById(id);
        return Result.OK("删除成功!");
    }
}
