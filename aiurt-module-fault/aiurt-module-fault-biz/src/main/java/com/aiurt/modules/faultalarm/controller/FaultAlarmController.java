package com.aiurt.modules.faultalarm.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.modules.faultalarm.dto.req.AlmRecordReqDTO;
import com.aiurt.modules.faultalarm.dto.req.CancelAlarmReqDTO;
import com.aiurt.modules.faultalarm.dto.resp.AlmRecordRespDTO;
import com.aiurt.modules.faultalarm.service.IFaultAlarmService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author:wgp
 * @create: 2023-06-05 09:46
 * @Description: 集中告警管理
 */
@Api(tags = "集中告警管理")
@RestController
@RequestMapping("/fault/alarm")
@Slf4j
public class FaultAlarmController {

    @Resource
    private IFaultAlarmService faultAlarmService;

    /**
     * 查询处理过的告警记录的分页列表
     *
     * @param almRecordReqDto 请求DTO，包含查询条件
     * @param pageNo          当前页码，默认为1
     * @param pageSize        每页显示的记录数，默认为10
     * @return 响应结果，包含分页后的处理过的告警记录列表
     */
    @AutoLog(value = "查询", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "查询处理过的告警记录的分页列表", notes = "查询处理过的告警记录的分页列表")
    @GetMapping(value = "/list")
    public Result<IPage<AlmRecordRespDTO>> queryAlarmRecordPageList(AlmRecordReqDTO almRecordReqDto, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<AlmRecordRespDTO> pageList = faultAlarmService.queryAlarmRecordPageList(almRecordReqDto, pageNo, pageSize);
        return Result.OK(pageList);
    }

    /**
     * 取消告警
     *
     * @param cancelAlarmReqDTO 取消告警请求DTO
     * @return 响应结果，包含取消告警操作的结果信息
     */
    @AutoLog(value = "取消告警", operateType = 3, operateTypeAlias = "取消告警")
    @ApiOperation(value = "取消告警", notes = "取消告警")
    @RequestMapping(value = "/cancelAlarm", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> cancelAlarm(@RequestBody CancelAlarmReqDTO cancelAlarmReqDTO) {
        faultAlarmService.cancelAlarm(cancelAlarmReqDTO);
        return Result.OK("取消告警成功!");
    }

    /**
     * 获取告警记录详情
     *
     * @param id 告警记录ID
     * @return 响应结果，包含告警记录的详情信息
     */
    @AutoLog(value = "获取告警记录详情", operateType = 1, operateTypeAlias = "获取告警记录详情", module = ModuleType.FAULT)
    @ApiOperation(value = "获取告警记录详情", notes = "获取告警记录详情")
    @GetMapping(value = "/alarmDetails")
    public Result<AlmRecordRespDTO> alarmDetails(@RequestParam("告警记录id") String id) {
        AlmRecordRespDTO result = faultAlarmService.faultAlarmService(id);
        return Result.OK(result);
    }
}
