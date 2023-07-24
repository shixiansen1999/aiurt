package com.aiurt.boot.bigscreen.controller;

import com.aiurt.boot.bigscreen.service.BigscreenPlanService;
import com.aiurt.boot.index.dto.*;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1315:33
 */
@Api(tags = "大屏检修接口")
@RestController
@RequestMapping("/plan/bigscreen")
@Slf4j
public class BigscreenPlanController {
    @Resource
    private BigscreenPlanService bigscreenPlanService;

    /**
     * 获取大屏的检修重要数据展示
     *
     * @param lineCode 线路code
     * @return
     */
    @AutoLog(value = "获取大屏的检修概况数量", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "获取大屏的检修概况数量", notes = "获取大屏的检修概况数量")
    @RequestMapping(value = "/overviewInfo", method = RequestMethod.GET)
    public Result<PlanIndexDTO> getOverviewInfo(@ApiParam(name = "lineCode", value = "线路code,多个用,隔开") @RequestParam(value = "lineCode", required = false) String lineCode,
                                                @ApiParam(name = "startDate", value = "开始时间") String startDate,
                                                @ApiParam(name = "endDate", value = "结束时间") String endDate) {
        PlanIndexDTO result = bigscreenPlanService.getOverviewInfo(lineCode, startDate,endDate);
        return Result.OK(result);
    }

    /**
     * 功能：巡检修数据分析->检修数据统计（带分页）
     *
     * @param lineCode 线路code
     * @param item     1计划数，2完成数，3漏检数，4今日检修数
     * @return
     */
    @AutoLog(value = "巡检修数据分析-检修数据统计", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "巡检修数据分析-检修数据统计（带分页）", notes = "巡检修数据分析-检修数据统计（带分页）")
    @RequestMapping(value = "/getInspectionDataPage", method = RequestMethod.GET)
    public Result<IPage<InspectionDTO>> getInspectionDataPage(@ApiParam(name = "lineCode", value = "线路code,多个用,隔开") @RequestParam(value = "lineCode", required = false) String lineCode,
                                                              @ApiParam(name = "item", value = "1计划数，2完成数，3漏检数，4今日检修数,5今日检修已完成,6未完成数") @RequestParam(value = "item", required = false) Integer item,
                                                              @ApiParam(name = "startDate", value = "开始日期") String startDate,
                                                              @ApiParam(name = "endDate", value = "结束日期") String endDate,
                                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<InspectionDTO> page = new Page<>(pageNo, pageSize);
        IPage<InspectionDTO> result = bigscreenPlanService.getInspectionDataPage(lineCode,startDate,endDate, item, page);
        return Result.OK(result);
    }

    /**
     * 功能：巡检修数据分析-检修数据统计（不带分页）
     *
     * @param lineCode 线路code
     * @param type     类型:1：本周，2：上周，3：本月， 4：上月
     * @param item     1计划数，2完成数，3漏检数，4今日检修数
     * @return
     */
    @AutoLog(value = "巡检修数据分析-检修数据统计", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "巡检修数据分析-检修数据统计（不带分页）", notes = "巡检修数据分析-检修数据统计（不带分页）")
    @RequestMapping(value = "/getInspectionDataNoPage", method = RequestMethod.GET)
    public Result<List<InspectionDTO>> getInspectionDataNoPage(@ApiParam(name = "lineCode", value = "线路code,多个用,隔开") @RequestParam(value = "lineCode", required = false) String lineCode,
                                                               @ApiParam(name = "type", value = "类型:1：本周，2：上周，3：本月， 4：上月", defaultValue = "1") @RequestParam(value = "type",required = false) String type,
                                                               @ApiParam(name = "item", value = "1计划数，2完成数，3漏检数，4今日检修数") @RequestParam(value = "item",required = false) Integer item) {
        List<InspectionDTO> result = bigscreenPlanService.getInspectionDataNoPage(lineCode, type, item);
        return Result.OK(result);
    }

    /**
     * 功能：巡检修数据分析->检修任务完成情况
     *
     * @param lineCode 线路code
     * @return
     */
    @AutoLog(value = "巡检修数据分析-检修任务完成情况", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "巡检修数据分析-检修任务完成情况", notes = "巡检修数据分析-检修任务完成情况")
    @RequestMapping(value = "/getTaskCompletion", method = RequestMethod.GET)
    public Result<List<PlanIndexDTO>> getTaskCompletion(@ApiParam(name = "lineCode", value = "线路code,多个用,隔开") @RequestParam(value = "lineCode", required = false) String lineCode,
                                                        @ApiParam(name = "startDate", value = "开始时间") String startDate,
                                                        @ApiParam(name = "endDate", value = "结束时间") String endDate) {
        List<PlanIndexDTO> result = bigscreenPlanService.getTaskCompletion(lineCode,startDate,endDate);
        return Result.OK(result);
    }


    /**
     * 功能：班组画像
     *
     * @return
     */
    @AutoLog(value = "班组画像", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "班组画像", notes = "班组画像")
    @RequestMapping(value = "/getTeamPortrait", method = RequestMethod.GET)
    public Result<List<TeamPortraitDTO>> getTeamPortrait(@ApiParam(name = "type", value = "类型:1：本周，2：上周，3：本月， 4：上月", defaultValue = "1") @RequestParam(value = "type", required = false) Integer type) {
        if (type == null) {
            type = 1;
        }
        List<TeamPortraitDTO> result = bigscreenPlanService.getTeamPortrait(type);
        return Result.OK(result);
    }

    /**
     * 功能：班组画像详情
     *
     * @param type 类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    @AutoLog(value = "班组画像详情", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "班组画像详情", notes = "班组画像详情")
    @RequestMapping(value = "/getTeamPortraitDetails", method = RequestMethod.GET)
    public Result<IPage<TeamUserDTO>> getTeamPortraitDetails(@ApiParam(name = "type", value = "类型:1：本周，2：上周，3：本月， 4：上月", defaultValue = "1") @RequestParam(value = "type",required = false) Integer type,
                                                                    @ApiParam(name = "teamId", value = "班组id") @RequestParam(value = "teamId",required = false) String teamId,
                                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (type == null) {
            type = 1;
        }
        IPage<TeamUserDTO> teamPortraitDetails = bigscreenPlanService.getTeamPortraitDetails(type, teamId, pageNo, pageSize);
        return Result.OK(teamPortraitDetails);
    }
}
