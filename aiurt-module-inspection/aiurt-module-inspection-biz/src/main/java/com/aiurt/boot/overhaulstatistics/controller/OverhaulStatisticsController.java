package com.aiurt.boot.overhaulstatistics.controller;

import com.aiurt.boot.overhaulstatistics.service.OverhaulStatisticsService;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.fault.dto.FaultStatisticsDTO;
import com.aiurt.modules.faultanalysisreport.dto.SpareConsumeDTO;
import com.aiurt.modules.train.task.vo.ReportReqVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zwl
 * @Title:
 * @Description: 检修统计分析控制层
 * @date 2022/9/2011:14
 */
@Api(tags = "检修-统计分析")
@RestController
@RequestMapping("/overhaul/statistics")
@Slf4j
public class OverhaulStatisticsController {


    @Autowired
    private OverhaulStatisticsService overhaulStatisticsService;

    /**
     * 检修-统计分析查询
     * @param condition
     * @return
     */
    @AutoLog(value = "检修-统计分析查询", operateType = 1, operateTypeAlias = "检修-统计分析查询")
    @ApiOperation(value = "检修-统计分析查询", notes = "检修-统计分析查询")
    @RequestMapping(value = "/getOverhaulList", method = RequestMethod.GET)
    public Result<List<OverhaulStatisticsDTO>> getOverhaulList(OverhaulStatisticsDTO condition) {
        List<OverhaulStatisticsDTO> overhaulList = overhaulStatisticsService.getOverhaulList(condition);
        return Result.OK(overhaulList);
    }

    /**
     * 统计分析-检修报表导出
     *
     * @param request
     * @return
     */
    @AutoLog(value = "统计分析-检修报表导出")
    @ApiOperation(value = "统计分析-检修报表导出", notes = "统计分析-检修报表导出")
    @GetMapping(value = "/reportExport")
    public ModelAndView reportExport(HttpServletRequest request, OverhaulStatisticsDTO overhaulStatisticsDTO) {
        return overhaulStatisticsService.reportExport(request, overhaulStatisticsDTO);
    }
}
