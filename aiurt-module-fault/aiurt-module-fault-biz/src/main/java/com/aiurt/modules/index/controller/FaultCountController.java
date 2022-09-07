package com.aiurt.modules.index.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.index.service.IFaultCountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 首页故障概况接口
 *
 * @author: qkx
 * @date: 2022年09月05日 15:51
 */

@Api(tags = "首页故障概况接口")
@RestController
@RequestMapping("/fault/faultCount")
@Slf4j
public class FaultCountController {
    @Autowired
    IFaultCountService faultCountService;

    /**
     * 获取首页的故障概况信息
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @AutoLog(value = "首页-故障概况")
    @ApiOperation(value="故障概况", notes="故障概况")
    @GetMapping(value = "/queryFaultCount")
    public Result<FaultIndexDTO> queryFaultCount(@ApiParam(name = "startDate", value = "开始日期") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                @ApiParam(name = "endDate", value = "结束日期") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate){
        FaultIndexDTO faultIndexDTO = faultCountService.queryFaultCount(startDate, endDate);
        return Result.ok(faultIndexDTO);
    }

}
