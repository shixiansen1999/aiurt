package com.aiurt.boot.modules.repairManage.controller;

import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.repairManage.service.IStatisticsService;
import com.aiurt.boot.modules.repairManage.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/27 17:01
 */
@Slf4j
@Api(tags = "检修数据统计")
@RestController
@RequestMapping("/repairManage/statistics")
public class RepairStatisticsController {

    @Autowired
    private IStatisticsService statisticsService;

    @ApiOperation(value = "检修数据统计-工作量统计", notes = "检修数据统计-工作量统计")
    @PostMapping("/workload")
    public Result workload(@RequestBody @Validated StatisticsQueryVO statisticsQueryVO) {
        return statisticsService.workload(statisticsQueryVO);
    }

    @ApiOperation(value = "检修数据统计-检修项统计", notes = "检修数据统计-检修项统计")
    @RequestMapping("/repairItem")
    public Result repairItem() {
        return statisticsService.repairItem();
    }

    @ApiOperation(value = "检修数据统计-班组检修对比", notes = "检修数据统计-班组检修对比")
    @RequestMapping("/compareToTeam")
    public Result compareToTeam(@RequestBody @Validated TimeVO timeVO) {
        return statisticsService.compareToTeam(timeVO);
    }

    @ApiOperation(value = "导出excel", notes = "导出excel-请求参数参考工作量统计")
    @PostMapping(value = "/exportXls")
    public ModelAndView exportXls(@RequestBody @Validated StatisticsQueryVO statisticsQueryVO) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        final Result result = statisticsService.workload(statisticsQueryVO);
        final ArrayList<WorkLoadVO> list = (ArrayList<WorkLoadVO>) result.getResult();
        mv.addObject(NormalExcelConstants.FILE_NAME, "检修工作量统计");
        mv.addObject(NormalExcelConstants.CLASS, WorkLoadVO.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("检修工作量统计", "导出时间:"+ LocalDate.now(), ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    @ApiOperation(value = "导出excel", notes = "导出excel")
    @RequestMapping(value = "/repairItem/exportXls")
    public ModelAndView exportXlss() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        final Result repairItem = statisticsService.repairItem();
        final RepairItemVO repairItemVO = (RepairItemVO) repairItem.getResult();
        final ArrayList<RepairItemVO> list = new ArrayList<>();
        list.add(repairItemVO);
        mv.addObject(NormalExcelConstants.FILE_NAME, "检修项统计");
        mv.addObject(NormalExcelConstants.CLASS, RepairItemVO.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("检修项统计", "导出时间:"+ LocalDate.now(), ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

}
