package com.aiurt.modules.index.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultlevel.entity.FaultLevel;
import com.aiurt.modules.faultlevel.service.IFaultLevelService;
import com.aiurt.modules.index.service.IFaultCountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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

import static com.aiurt.modules.fault.controller.FaultController.PERMISSION_URL;

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

    public static final String  PERMISSION_URL = "/fault/faultCount";
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
     * 首页-故障概况详情(总数和已解决)
     * @param faultCountInfoReq
     * @return
     */
    @AutoLog(value = "首页-故障概况详情(总数和已解决)", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-故障概况详情(总数和已解决)", notes = "首页-故障概况详情(总数和已解决)")
    @RequestMapping(value = "/getFaultCountInfo", method = RequestMethod.GET)
    public Result<IPage<FaultCountInfoDTO>> getFaultCountInfo(@Validated FaultCountInfoReq faultCountInfoReq)
    {
        IPage<FaultCountInfoDTO> result = faultCountService.getFaultCountInfo(faultCountInfoReq);
        return Result.OK(result);
    }

    @AutoLog(value = "首页-故障概况详情(未解决和挂起数)", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-故障概况详情(未解决和挂起数)", notes = "首页-故障概况详情(未解决和挂起数)")
    @RequestMapping(value = "/getFaultCountInfos", method = RequestMethod.GET)
    public Result<IPage<FaultCountInfosDTO>> getFaultCountInfos(@Validated FaultCountInfoReq faultCountInfoReq)
    {
        IPage<FaultCountInfosDTO> result = faultCountService.getFaultCountInfos(faultCountInfoReq);
        return Result.OK(result);
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

    /**
     * 故障概况分页列表详情
     *
     * @param fault
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "故障概况分页列表详情", operateType =  1, operateTypeAlias = "查询", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "故障概况分页列表详情", notes = "故障概况分页列表详情")
    @GetMapping(value = "/queryFaultInfo")
    public Result<IPage<Fault>> queryFaultInfo(Fault fault,
                                              @ApiParam(name = "startDate", value = "开始日期") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                              @ApiParam(name = "endDate", value = "结束日期") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        String stationCode = fault.getStationCode();
        if (StrUtil.isNotBlank(stationCode)) {
            fault.setStationCode(null);
        }
        String faultPhenomenon = fault.getFaultPhenomenon();
        if (StrUtil.isNotBlank(faultPhenomenon)) {
            fault.setFaultPhenomenon(null);
        }
        String appointUserName = fault.getAppointUserName();
        if (StrUtil.isNotBlank(appointUserName)) {
            fault.setAppointUserName(null);
        }
        String statusCondition = fault.getStatusCondition();
        if (StrUtil.isNotBlank(statusCondition)) {
            fault.setStatusCondition(null);
        }
        QueryWrapper<Fault> queryWrapper = QueryGenerator.initQueryWrapper(fault, req.getParameterMap());
        Page<Fault> page = new Page<>(pageNo, pageSize);
        queryWrapper.apply(StringUtils.isNotBlank((CharSequence) startDate),"DATE(approval_pass_time) >= STR_TO_DATE(startData)");
        queryWrapper.apply(StringUtils.isNotBlank((CharSequence) endDate),"DATE(approval_pass_time) <= STR_TO_DATE(endData)");
//        queryWrapper.apply("fault.getApprovalPassTime() <= endDate");
//        queryWrapper.apply(StrUtil.isNotBlank((CharSequence) fault.getApprovalPassTime()),"date_format(approval_pass_time,'%Y-%m-%d') &gt;=  date_format(startData,'%Y-%m-%d')\n" +
//                "                and date_format(approval_pass_time ,'%Y-%m-%d') &lt;= date_format(endDate,'%Y-%m-%d')",fault.getApprovalPassTime());
        //修改查询条件
        queryWrapper.apply(StrUtil.isNotBlank(stationCode), "(line_code = {0} or station_code = {0} or station_position_code = {0})", stationCode);
        queryWrapper.apply(StrUtil.isNotBlank(fault.getDevicesIds()), "(code in (select fault_code from fault_device where device_code like  concat('%', {0}, '%')))", fault.getDevicesIds());
        queryWrapper.apply(StrUtil.isNotBlank(faultPhenomenon), "(fault_phenomenon like concat('%', {0}, '%') or code like  concat('%', {0}, '%'))", faultPhenomenon);
        // 负责人查询
        queryWrapper.apply(StrUtil.isNotBlank(appointUserName), "( appoint_user_name in (select username from sys_user where (username like concat('%', {0}, '%') or realname like concat('%', {0}, '%'))))", appointUserName);
        queryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotBlank(statusCondition)) {
            queryWrapper.in("status", StrUtil.split(statusCondition, ','));
        }
        IPage<Fault> pageList = faultService.page(page, queryWrapper);

        List<Fault> records = pageList.getRecords();
        records.stream().forEach(fault1 -> {
            List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(fault1.getCode());
            // 权重登记
            if (StrUtil.isNotBlank(fault1.getFaultLevel())) {
                LambdaQueryWrapper<FaultLevel> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(FaultLevel::getCode, fault1.getFaultLevel()).last("limit 1");
                FaultLevel faultLevel = faultLevelService.getBaseMapper().selectOne(wrapper);
                if (Objects.isNull(faultLevel)) {
                    fault1.setWeight(0);
                }else {
                    String weight = faultLevel.getWeight();
                    if (StrUtil.isNotBlank(weight)) {
                        try {
                            fault1.setWeight(Integer.valueOf(weight));
                        } catch (NumberFormatException e) {
                            fault1.setWeight(0);
                        }
                    }else {
                        fault1.setWeight(0);
                    }
                }

            }else {
                fault1.setWeight(0);
            }
            fault1.setFaultDeviceList(faultDeviceList);

            // 是否重新指派
            LambdaQueryWrapper<FaultRepairRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FaultRepairRecord::getFaultCode, fault1.getCode());
            Long count = faultRepairRecordService.getBaseMapper().selectCount(lambdaQueryWrapper);
            fault1.setSignAgainFlag(count>0?1:0);
        });

        // todo 优化排序
        return Result.OK(pageList);
    }
}
