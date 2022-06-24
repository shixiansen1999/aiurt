package com.aiurt.boot.plan.controller;

import com.aiurt.boot.plan.dto.ListDTO;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.boot.plan.dto.RepairStrategyDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.rep.RepairStrategyReq;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Api(tags = "检修计划池")
@RestController
@RequestMapping("/plan/repairPool")
@Slf4j
public class RepairPoolController extends BaseController<RepairPool, IRepairPoolService> {
    @Autowired
    private IRepairPoolService repairPoolService;

    /**
     * 检修计划池列表查询
     *
     * @return
     */
    @AutoLog(value = "检修计划池列表查询")
    @ApiOperation(value = "检修计划池列表查询", notes = "检修计划池列表查询")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairPool.class)
    })
    @GetMapping(value = "/list")
    public Result<List<RepairPool>> queryList(@RequestParam @ApiParam(required = true, value = "开始时间", name = "startTime") Date startTime,
                                              @RequestParam @ApiParam(required = true, value = "结束时间", name = "endTime") Date endTime) {
        List<RepairPool> repairPoolList = repairPoolService.queryList(startTime, endTime);
        return Result.OK(repairPoolList);
    }

    /**
     * 添加
     *
     * @param repairPool
     * @return
     */
    @AutoLog(value = "repair_pool-添加")
    @ApiOperation(value = "repair_pool-添加", notes = "repair_pool-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody RepairPool repairPool) {
        repairPoolService.save(repairPool);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param repairPool
     * @return
     */
    @AutoLog(value = "repair_pool-编辑")
    @ApiOperation(value = "repair_pool-编辑", notes = "repair_pool-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody RepairPool repairPool) {
        repairPoolService.updateById(repairPool);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "repair_pool-通过id删除")
    @ApiOperation(value = "repair_pool-通过id删除", notes = "repair_pool-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        repairPoolService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "repair_pool-批量删除")
    @ApiOperation(value = "repair_pool-批量删除", notes = "repair_pool-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.repairPoolService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过检修计划id查看检修标准详情
     *
     * @param req
     * @return
     */
    @AutoLog(value = "通过检修计划id查看检修标准详情")
    @ApiOperation(value = "通过检修计划id查看检修标准详情", notes = "通过检修计划id查看检修标准详情")
    @GetMapping(value = "/queryStandardById")
    public Result<RepairStrategyDTO> queryStandardById(RepairStrategyReq req) {
        RepairStrategyDTO repairStrategyDTOList = repairPoolService.queryStandardById(req);
        return Result.OK(repairStrategyDTOList);
    }

    /**
     * 通过检修计划id查看详情
     *
     * @param id
     * @return
     */
    @AutoLog(value = "通过检修计划id查看详情")
    @ApiOperation(value = "通过检修计划id查看详情", notes = "通过检修计划id查看详情")
    @GetMapping(value = "/queryById")
    public Result<RepairPoolDetailsDTO> queryById(@RequestParam @ApiParam(name = "id", required = true, value = "检修计划id") String id) {
        RepairPoolDetailsDTO repairPool = repairPoolService.queryById(id);
        return Result.OK(repairPool);
    }

    /**
     * 根据年份获取时间范围和周数
     *
     * @param year
     * @return
     */
    @AutoLog(value = "根据年份获取时间范围和周数")
    @ApiOperation(value = "根据年份获取时间范围和周数", notes = "根据年份获取时间范围和周数")
    @GetMapping(value = "/getTimeInfo")
    public Result getTimeInfo(@RequestParam @ApiParam(name = "year", value = "年份", required = true) Integer year) {
        return repairPoolService.getTimeInfo(year);
    }


    /**
     * 检修计划池-调整时间
     *
     * @param
     * @return
     */
    @AutoLog(value = "检修计划池-调整时间")
    @ApiOperation(value = "检修计划池-调整时间", notes = "检修计划池-调整时间")
    @PostMapping(value = "/updateTime")
    public Result updateTime(@RequestParam(name = "ids") String ids,
                             @RequestParam(name = "startTime") String startTime,
                             @RequestParam(name = "endTime") String endTime) {
        return repairPoolService.updateTime(ids, startTime, endTime);
    }

    /**
     * 检修详情里的适用专业下拉列表
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修详情里的适用专业下拉列表")
    @ApiOperation(value = "检修详情里的适用专业下拉列表", notes = "检修详情里的适用专业下拉列表")
    @GetMapping(value = "/queryMajorList")
    public Result<List<ListDTO>> queryMajorList(@RequestParam @ApiParam(name = "id", required = true, value = "检修计划id") String id) {
        return null;
    }

    /**
     * 检修详情里的适用专业子系统下拉列表
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修详情里的适用专业子系统下拉列表")
    @ApiOperation(value = "检修详情里的适用专业子系统下拉列表", notes = "检修详情里的适用专业子系统下拉列表")
    @GetMapping(value = "/querySystemList")
    public Result<List<ListDTO>> querySystemList(@RequestParam @ApiParam(name = "id", required = true, value = "检修计划id") String id) {
        return null;
    }


    /**
     * 检修详情里的检修标准下拉列表
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修详情里的检修标准下拉列表")
    @ApiOperation(value = "检修详情里的检修标准下拉列表", notes = "检修详情里的检修标准下拉列表")
    @GetMapping(value = "/queryStandardList")
    public Result<List<ListDTO>> queryStandardList(@RequestParam @ApiParam(name = "id", required = true, value = "检修计划id") String id) {
        return null;
    }


}
