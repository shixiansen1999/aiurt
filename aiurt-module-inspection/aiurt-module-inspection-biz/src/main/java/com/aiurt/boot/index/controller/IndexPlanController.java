package com.aiurt.boot.index.controller;

import com.aiurt.boot.index.dto.DayTodoDTO;
import com.aiurt.boot.index.dto.PlanIndexDTO;
import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.boot.index.service.IndexPlanService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
     * 获取首页的检修概况信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    @AutoLog(value = "首页-检修概况", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-检修概况", notes = "首页-检修概况")
    @RequestMapping(value = "/overviewInfo", method = RequestMethod.GET)
    public Result<PlanIndexDTO> getOverviewInfo(@ApiParam(name = "startDate", value = "开始日期") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                @ApiParam(name = "endDate", value = "结束日期") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        PlanIndexDTO result = indexPlanService.getOverviewInfo(startDate, endDate);
        return Result.OK(result);
    }

    /**
     * 获取首页的检修概况详情
     *
     * @param pageNo         第几页
     * @param pageSize       每页显示多少数据
     * @param type           类型：1总数2已检修3未检修4漏检
     * @param taskDetailsReq 查询条件
     * @return
     */
    @AutoLog(value = "首页-检修概况详情", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-检修概况详情", notes = "首页-检修概况详情")
    @RequestMapping(value = "/getOverviewInfoDetails", method = RequestMethod.POST)
    public Result<IPage<TaskDetailsDTO>> getOverviewInfoDetails(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                @ApiParam(name = "type", value = "类型：1总数2已检修3未检修4漏检") @RequestParam("type") Integer type,
                                                                @ApiParam(name = "taskDetailsReq", value = "查询条件") @RequestParam("taskDetailsReq") @Validated TaskDetailsReq taskDetailsReq

    ) {
        IPage<TaskDetailsDTO> result = indexPlanService.getOverviewInfoDetails(pageNo, pageSize, type, taskDetailsReq);
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
        List<DayTodoDTO> result = indexPlanService.getUserSchedule(year,month);
        return Result.OK(result);
    }
}
