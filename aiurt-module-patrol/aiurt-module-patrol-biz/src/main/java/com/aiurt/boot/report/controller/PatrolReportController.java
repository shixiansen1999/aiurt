package com.aiurt.boot.report.controller;

import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
import com.aiurt.boot.report.service.PatrolReportService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/9/19
 * @desc
 */
@Api(tags = "巡视报表")
@RestController
@RequestMapping("/report")
@Slf4j
public class PatrolReportController {
    @Autowired
    private PatrolReportService reportService;
    /**
     * 统计报表-巡视数据统计
     * @param report
     * @return
     */
    @AutoLog(value = "统计报表-巡视数据统计", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-巡视数据统计", notes = "统计报表-巡视数据统计")
    @RequestMapping(value = "/patrolTaskList", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<IPage<PatrolReport>> getStatisticsDate(PatrolReportModel report,
                                                         @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Page<PatrolReport> pageList = new Page<>(pageNo, pageSize);
        pageList = reportService.getTaskDate(pageList, report);

        return Result.ok(pageList);
    }
}
