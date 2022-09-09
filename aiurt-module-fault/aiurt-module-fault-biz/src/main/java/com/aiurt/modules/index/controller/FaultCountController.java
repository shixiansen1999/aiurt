package com.aiurt.modules.index.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.fault.dto.FaultTimeoutLevelDTO;
import com.aiurt.modules.fault.dto.FaultTimeoutLevelReq;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultlevel.entity.FaultLevel;
import com.aiurt.modules.faultlevel.service.IFaultLevelService;
import com.aiurt.modules.index.service.IFaultCountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Autowired
    private IFaultService faultService;

    @Autowired
    private IFaultDeviceService faultDeviceService;

    @Autowired
    private IFaultLevelService faultLevelService;

    @Autowired
    private IFaultRepairRecordService faultRepairRecordService;

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

    /**
     * 首页-故障超时等级详情接口
     *
     * @param faultTimeoutLevelReq 查询条件
     * @return
     */
    @AutoLog(value = "首页-故障超时等级详情", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-故障超时等级详情", notes = "首页-故障超时等级详情")
    @RequestMapping(value = "/getFaultLevelInfo", method = RequestMethod.GET)
    public Result<IPage<FaultTimeoutLevelDTO>> getFaultLevelInfo(@Validated FaultTimeoutLevelReq faultTimeoutLevelReq)
    {
        IPage<FaultTimeoutLevelDTO> result = faultCountService.getFaultLevelInfo(faultTimeoutLevelReq);
        return Result.OK(result);
    }

    /**
     * 代办事项故障情况
     * @param startDate
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "首页-代办事项故障情况", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-代办事项故障情况", notes = "首页-代办事项故障情况")
    @RequestMapping(value = "/getMainFaultCondition", method = RequestMethod.GET)
    public Result<IPage<FaultTimeoutLevelDTO>> getMainFaultCondition(@ApiParam(name = "startDate", value = "开始日期yyyy-MM-dd") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<FaultTimeoutLevelDTO> page = new Page<>(pageNo,pageSize);
        IPage<FaultTimeoutLevelDTO> mainFaultCondition = faultCountService.getMainFaultCondition(page, startDate);
        return Result.OK(mainFaultCondition);
    }
}
