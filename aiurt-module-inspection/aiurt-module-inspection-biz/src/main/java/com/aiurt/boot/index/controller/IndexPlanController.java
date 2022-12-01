package com.aiurt.boot.index.controller;

import com.aiurt.boot.index.dto.DayTodoDTO;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.boot.index.service.IndexPlanService;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description: 首页检修接口入口
 * @date 2022/9/515:04
 */
@Api(tags = "首页检修接口")
@RestController
@RequestMapping("/plan/index")
@Slf4j
public class IndexPlanController {
    @Resource
    private IndexPlanService indexPlanService;

    /**
     * 获取首页的检修概况数量
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    @AutoLog(value = "首页-获取首页的检修概况数量", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-获取首页的检修概况数量", notes = "首页-获取首页的检修概况数量")
    @RequestMapping(value = "/overviewInfo", method = RequestMethod.GET)
    @PermissionData
    public Result<PlanIndexDTO> getOverviewInfo(@ApiParam(name = "isAllData", value = "是否全部数据0否1是",defaultValue = "0") @RequestParam("isAllData") Integer isAllData,
                                                @ApiParam(name = "startDate", value = "开始日期yyyy-MM-dd") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                @ApiParam(name = "endDate", value = "结束日期yyyy-MM-dd") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        PlanIndexDTO result = indexPlanService.getOverviewInfo(isAllData,startDate, endDate);
        return Result.OK(result);
    }

    /**
     * 获取首页的检修概况详情
     *
     * @param taskDetailsReq 查询条件
     * @return
     */
    @AutoLog(value = "首页-获取首页的检修概况详情", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-获取首页的检修概况详情", notes = "首页-获取首页的检修概况详情")
    @RequestMapping(value = "/getOverviewInfoDetails", method = RequestMethod.GET)
    public Result<IPage<TaskDetailsDTO>> getOverviewInfoDetails(@Validated TaskDetailsReq taskDetailsReq

    ) {
        IPage<TaskDetailsDTO> result = indexPlanService.getOverviewInfoDetails(taskDetailsReq);
        return Result.OK(result);
    }

    /**
     * 点击站点获取检修计划数据
     *
     * @param taskDetailsReq 查询条件
     * @return
     */
    @AutoLog(value = "首页-点击站点获取检修数据", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-点击站点获取检修数据", notes = "首页-点击站点获取检修数据")
    @RequestMapping(value = "/getMaintenancDataByStationCode", method = RequestMethod.GET)
    public Result<IPage<RepairPoolDetailsDTO>> getMaintenancDataByStationCode(@Validated TaskDetailsReq taskDetailsReq) {
        IPage<RepairPoolDetailsDTO> result = indexPlanService.getMaintenancDataByStationCode(taskDetailsReq);
        return Result.OK(result);
    }

    /**
     * 获取首页的日代办事项
     *
     * @param year  年份
     * @param month 月份
     * @return
     */
    @AutoLog(value = "首页-日代办事项", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-日代办事项", notes = "首页-日代办事项")
    @RequestMapping(value = "/getUserSchedule", method = RequestMethod.GET)
    public Result<List<DayTodoDTO>> getUserSchedule(@ApiParam(value = "年份") @RequestParam(name = "year") Integer year,
                                                    @ApiParam(value = "月份") @RequestParam(name = "month") Integer month
    ) {
        List<DayTodoDTO> result = indexPlanService.getUserSchedule(year, month);
        return Result.OK(result);
    }


    /**
     * 代办事项检修情况
     * @param startDate
     * @param pageNo
     * @param pageSize
     * @param stationCode
     * @return
     */
    @AutoLog(value = "首页-代办事项检修情况", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-代办事项检修情况", notes = "首页-代办事项检修情况")
    @RequestMapping(value = "/getMaintenanceSituation", method = RequestMethod.GET)
    public Result<IPage<RepairPoolDetailsDTO>> getMaintenanceSituation(@ApiParam(name = "startDate", value = "开始日期yyyy-MM-dd") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                       @ApiParam(name = "stationCode", value = "站点") @RequestParam(value = "stationCode", required = false) String stationCode
    ) {
        Page<RepairPoolDetailsDTO> page = new Page<>(pageNo,pageSize);
        IPage<RepairPoolDetailsDTO> maintenanceSituation = indexPlanService.getMaintenanceSituation(page, startDate, stationCode);
        return Result.OK(maintenanceSituation);
    }
}
