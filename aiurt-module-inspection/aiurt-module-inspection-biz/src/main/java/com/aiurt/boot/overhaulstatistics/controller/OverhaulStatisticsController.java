package com.aiurt.boot.overhaulstatistics.controller;

import com.aiurt.boot.overhaulstatistics.service.OverhaulStatisticsService;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTO;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTOS;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public Result<IPage<OverhaulStatisticsDTOS>> getOverhaulList(OverhaulStatisticsDTOS condition,
                                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                                 HttpServletRequest req) {
        Page<OverhaulStatisticsDTOS> pageList = new Page<>(pageNo, pageSize);
        Page<OverhaulStatisticsDTOS> overhaulList = overhaulStatisticsService.getOverhaulList(pageList, condition);
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
    public ModelAndView reportExport(HttpServletRequest request, OverhaulStatisticsDTOS overhaulStatisticsDTO) {
        return overhaulStatisticsService.reportExport(request, overhaulStatisticsDTO);
    }
}
