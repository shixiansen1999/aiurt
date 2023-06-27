package com.aiurt.modules.worklog.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.modules.worklog.dto.WorkLogIndexDTO;
import com.aiurt.modules.worklog.service.IWorkLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 首页-工作日志
 *
 * @author 华宜威
 * @date 2023-06-25 17:04:50
 */
@Slf4j
@Api(tags = "工作日志")
@RestController
@RequestMapping("/worklog/workLogIndex")
public class WorkLogIndexController {

    @Autowired
    private IWorkLogService workLogDepotService;

    /**
     * 获取首页-工作日志
     * 获取首页工作日志的信息
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param request   request
     * @return
     */
    @AutoLog(value = "首页-工作日志", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-工作日志", notes = "首页-工作日志")
    @RequestMapping(value = "/overviewInfo", method = RequestMethod.GET)
    public Result<WorkLogIndexDTO> getOverviewInfo(@ApiParam(name = "startDate", value = "开始日期yyyy-MM-dd")
                                                   @RequestParam("startDate")
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                   @ApiParam(name = "endDate", value = "结束日期yyyy-MM-dd")
                                                   @RequestParam("endDate")
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                                   HttpServletRequest request) {
        WorkLogIndexDTO workLogIndexDTO = workLogDepotService.getOverviewInfo(startDate, endDate, request);
        return Result.ok(workLogIndexDTO);
    }
}
