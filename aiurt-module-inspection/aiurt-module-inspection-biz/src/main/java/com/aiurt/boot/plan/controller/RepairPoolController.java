package com.aiurt.boot.plan.controller;

import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.rep.RepairStrategyReq;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Resource
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
    public Result<List<RepairPool>> queryList(@RequestParam @ApiParam(required = true, value = "开始时间", name = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                              @RequestParam @ApiParam(required = true, value = "结束时间", name = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        List<RepairPool> repairPoolList = repairPoolService.queryList(startTime, endTime);
        return Result.OK(repairPoolList);
    }


//    /**
//     * 编辑
//     *
//     * @param repairPool
//     * @return
//     */
//    @AutoLog(value = "repair_pool-编辑")
//    @ApiOperation(value = "repair_pool-编辑", notes = "repair_pool-编辑")
//    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
//    public Result<String> edit(@RequestBody RepairPool repairPool) {
//        repairPoolService.updateById(repairPool);
//        return Result.OK("编辑成功!");
//    }
//
//
//    /**
//     * 批量删除
//     *
//     * @param ids
//     * @return
//     */
//    @AutoLog(value = "repair_pool-批量删除")
//    @ApiOperation(value = "repair_pool-批量删除", notes = "repair_pool-批量删除")
//    @DeleteMapping(value = "/deleteBatch")
//    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
//        this.repairPoolService.removeByIds(Arrays.asList(ids.split(",")));
//        return Result.OK("批量删除成功!");
//    }

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
     * 检修详情里的适用专业和专业子系统级联下拉列表
     *
     * @param code
     * @return
     */
    @AutoLog(value = "检修详情里的适用专业下拉列表")
    @ApiOperation(value = "检修详情里的适用专业下拉列表", notes = "检修详情里的适用专业下拉列表")
    @GetMapping(value = "/queryMajorList")
    public Result<List<MajorDTO>> queryMajorList(@RequestParam @ApiParam(name = "code", required = true, value = "检修计划code") String code) {
        List<MajorDTO> listDTOList = repairPoolService.queryMajorList(code);
        return Result.OK(listDTOList);
    }

    /**
     * 检修详情里的检修标准下拉列表
     *
     * @param code
     * @return
     */
    @AutoLog(value = "检修详情里的检修标准下拉列表")
    @ApiOperation(value = "检修详情里的检修标准下拉列表", notes = "检修详情里的检修标准下拉列表")
    @GetMapping(value = "/queryStandardList")
    public Result<List<StandardDTO>> queryStandardList(@RequestParam @ApiParam(name = "code", required = true, value = "检修计划code") String code) {
        List<StandardDTO> result = repairPoolService.queryStandardList(code);
        return Result.OK(result);
    }

    /**
     * 指派检修任务
     *
     * @param
     * @return
     */
    @AutoLog(value = "指派检修任务")
    @ApiOperation(value = "指派检修任务", notes = "指派检修任务")
    @PostMapping(value = "/assigned")
    public Result assigned(@RequestBody AssignDTO assignDTO) {
        return repairPoolService.assigned(assignDTO);
    }

    /**
     * 指派检修任务人员下拉列表
     *
     * @param
     * @return
     */
    @AutoLog(value = "指派检修任务人员下拉列表")
    @ApiOperation(value = "指派检修任务人员下拉列表", notes = "指派检修任务人员下拉列表")
    @GetMapping(value = "/queryUserList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = LoginUser.class)
    })
    public Result<List<LoginUser>> queryUserList(@RequestParam @ApiParam(name = "code", required = true, value = "检修计划code") String code) {
        List<LoginUser> loginUserList = repairPoolService.queryUserList(code);
        return Result.OK(loginUserList);
    }

    /**
     * 查询可用的计划令
     *
     * @param
     * @return
     */
    @AutoLog(value = "查询可用的计划令")
    @ApiOperation(value = "查询可用的计划令", notes = "查询可用的计划令")
    @GetMapping(value = "/queryPlanCodeList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = PlanCodeDTO.class)
    })
    public Result<List<PlanCodeDTO>> queryPlanCodeList(@RequestParam @ApiParam(name = "id", required = true, value = "检修计划id") String id) {
        List<PlanCodeDTO> planCodeDTOList = new ArrayList<>();
        return Result.OK(planCodeDTOList);
    }


}
