package com.aiurt.modules.fault.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.base.PageOrderGenerator;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultanalysisreport.service.IFaultAnalysisReportService;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.service.IFaultKnowledgeBaseTypeService;
import com.aiurt.modules.faultlevel.entity.FaultLevel;
import com.aiurt.modules.faultlevel.service.IFaultLevelService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Api(tags = "故障管理")
@RestController
@RequestMapping("/fault/")
@Slf4j
public class FaultController extends BaseController<Fault, IFaultService> {

    public static final String  PERMISSION_URL = "/fault/list";


    @Autowired
    private IFaultService faultService;

    @Autowired
    private IFaultDeviceService faultDeviceService;

    @Autowired
    private IFaultLevelService faultLevelService;

    @Autowired
    private IFaultRepairRecordService faultRepairRecordService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private IFaultAnalysisReportService faultAnalysisReportService;

    @Autowired
    private IFaultKnowledgeBaseService faultKnowledgeBaseService;

    @Autowired
    private IFaultKnowledgeBaseTypeService faultKnowledgeBaseTypeService;
    /**
     * 分页列表查询
     *
     * @param fault
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "查询", operateType =  1, operateTypeAlias = "查询", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "fault-分页列表查询", notes = "fault-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "fault/FaultList", appComponent="Breakdown/BreakdownList")
    public Result<IPage<Fault>> queryPageList(Fault fault,
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
        // 专业查询
        String subSystemCode = fault.getSubSystemCode();
        if (StrUtil.isNotBlank(subSystemCode)) {
            JSONObject csMajor = sysBaseAPI.getCsMajorByCode(subSystemCode);
            if (Objects.nonNull(csMajor)) {
                fault.setMajorCode(subSystemCode);
                fault.setSubSystemCode(null);
            }
        }

        // 故障等级处理， 不能模糊查询
        String f = fault.getFaultLevel();
        if (StrUtil.isNotBlank(f)) {
            fault.setFaultLevel(null);
        }

        //获取app输入故障现象查询内容，转换为code
        LambdaQueryWrapper<FaultKnowledgeBaseType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(FaultKnowledgeBaseType::getName, faultPhenomenon).eq(FaultKnowledgeBaseType::getDelFlag, 0);
        List<FaultKnowledgeBaseType> faultKnowledgeBaseTypes = faultKnowledgeBaseTypeService.getBaseMapper().selectList(wrapper);
        List<String> faultPhenomenonCodes = new ArrayList<>();
        if (CollUtil.isNotEmpty(faultKnowledgeBaseTypes)) {
            faultPhenomenonCodes = faultKnowledgeBaseTypes.stream().map(FaultKnowledgeBaseType::getCode).collect(Collectors.toList());
        }

        QueryWrapper<Fault> queryWrapper = QueryGenerator.initQueryWrapper(fault, req.getParameterMap());
        Page<Fault> page = new Page<>(pageNo, pageSize);
        PageOrderGenerator.initPage(page, fault, fault);
        //修改查询条件
        if (CollUtil.isNotEmpty(faultPhenomenonCodes)) {
            queryWrapper.in("fault_phenomenon", faultPhenomenonCodes);
            queryWrapper.or().like("code", faultPhenomenon);
        } else {
            if (StrUtil.isNotBlank(faultPhenomenon)) {
                queryWrapper.like("code", faultPhenomenon);
            }
        }
        queryWrapper.apply(StrUtil.isNotBlank(stationCode), "(line_code = {0} or station_code = {0} or station_position_code = {0})", stationCode);
        queryWrapper.apply(StrUtil.isNotBlank(fault.getDevicesIds()), "(code in (select fault_code from fault_device where device_code like  concat('%', {0}, '%')))", fault.getDevicesIds());
        // 负责人查询
        queryWrapper.apply(StrUtil.isNotBlank(appointUserName), "( appoint_user_name in (select username from sys_user where (username like concat('%', {0}, '%') or realname like concat('%', {0}, '%'))))", appointUserName);
        queryWrapper.orderByDesc("create_time");
        if (StrUtil.isNotBlank(statusCondition)) {
            queryWrapper.in("status", StrUtil.split(statusCondition, ','));
        }

        // 故障等级
        queryWrapper.eq(StrUtil.isNotBlank(f), "fault_level", f);
        IPage<Fault> pageList = faultService.page(page, queryWrapper);

        List<Fault> records = pageList.getRecords();
        dealResult(records);

        // todo 优化排序
        return Result.OK(pageList);
    }

    private void dealResult(List<Fault> records) {
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

            //判断是否已经进行过故障分析
            fault1.setIsFaultAnalysisReport(false);
            String code = fault1.getCode();
            LambdaQueryWrapper<FaultAnalysisReport> faultAnalysisReportWrapper = new LambdaQueryWrapper<>();
            faultAnalysisReportWrapper.eq(FaultAnalysisReport::getFaultCode, code);
            faultAnalysisReportWrapper.eq(FaultAnalysisReport::getDelFlag, 0).last("limit 1");
            FaultAnalysisReport faultAnalysisReport = faultAnalysisReportService.getBaseMapper().selectOne(faultAnalysisReportWrapper);
            //如果存在故障分析则返回true
            if (ObjectUtil.isNotNull(faultAnalysisReport)) {
                fault1.setIsFaultAnalysisReport(true);
            }

            if (CollUtil.isNotEmpty(faultDeviceList)) {
                List<String> collect = faultDeviceList.stream().map(FaultDevice::getDeviceName).collect(Collectors.toList());
                fault1.setDeviceName(CollUtil.join(collect,","));
            }
        });

    }

    /**
     * 添加
     *
     * @param fault
     * @return
     */
    @AutoLog(value = "新增故障上报", operateType =  2, operateTypeAlias = "故障上报", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "故障上报", notes = "故障上报")
    @PostMapping(value = "/add")
    public Result<?> add(@Validated @RequestBody Fault fault) {
        String faultCode = faultService.add(fault);
        return Result.OK("故障上报成功", faultCode);
    }

    /**
     * 审批
     *
     * @param approvalDTO
     * @return
     */
    @PutMapping("/approval")
    @ApiOperation(value = "故障审批", notes = "故障审批")
    @AutoLog(value = "上报审批", operateType =  3, operateTypeAlias = "上报审批", permissionUrl = PERMISSION_URL)
    public Result<?> approval(@RequestBody ApprovalDTO approvalDTO) {

        faultService.approval(approvalDTO);

        return Result.OK("操作成功");
    }

    /**
     * 编辑
     *
     * @param fault
     * @return
     */
    @AutoLog(value = "编辑", operateType =  3, operateTypeAlias = "编辑故障单",  permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "故障编辑", notes = "故障编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody Fault fault) {
        faultService.edit(fault);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过code作废
     *
     * @param cancelDTO
     * @return
     */

    @ApiOperation(value = "故障作废", notes = "故障作废")
    @AutoLog(value = "作废", operateType =  3, operateTypeAlias = "作废故障单",  permissionUrl = PERMISSION_URL)
    @PutMapping(value = "/cancel")
    public Result<String> cancel(@Valid @RequestBody CancelDTO cancelDTO) {
        faultService.cancel(cancelDTO);
        return Result.OK("作废成功!");
    }

    /**
     * 通过id查询
     *
     * @param code
     * @return
     */
    @AutoLog(value = "详情", operateType =  3, operateTypeAlias = "查看故障详情",  permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "通过故障编码查询详情", notes = "通过故障编码查询详情")
    @GetMapping(value = "/queryByCode")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "故障编码", required = true, paramType = "query")
    })
    public Result<Fault> queryByCode(@RequestParam(name = "code", required = true) String code) {
        Fault fault = faultService.queryByCode(code);
        if (fault == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(fault);
    }

    /**
     * 故障指派
     * @param assignDTO
     * @return
     */
    @AutoLog(value = "指派", operateType =  3, operateTypeAlias = "故障指派", module = ModuleType.FAULT)
    @ApiOperation(value = "故障指派", notes = "故障指派")
    @PutMapping("/assign")
    public Result<?> assign(@RequestBody AssignDTO assignDTO) {
        faultService.assign(assignDTO);
        return Result.OK("故障指派成功！");
    }

    /**
     * 领取故障工单
     * @param assignDTO
     * @return
     */
    @AutoLog(value = "领取故障工单", operateType = 3, operateTypeAlias = "领取故障工单",  permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "领取故障工单", notes = "领取故障工单")
    @PutMapping("/receive")
    public Result<?> receive(@RequestBody AssignDTO assignDTO) {
        faultService.receive(assignDTO);
        return Result.OK("领取故障工单成功");
    }

    /**
     *
     * @param assignDTO
     * @return
     */
    @AutoLog(value = "接收指派", operateType = 3, operateTypeAlias = "接收指派工单",  permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "接收指派", notes = "接收指派")
    @PutMapping("/receiveAssignment")
    public Result<?> receiveAssignment(@RequestBody AssignDTO assignDTO) {
        faultService.receiveAssignment(assignDTO.getFaultCode());
        return Result.OK("领取故障工单成功。");
    }

    /**
     * 拒收指派
     * @param refuseAssignmentDTO
     * @return
     */
    @AutoLog(value = "拒收指派", operateType = 3, operateTypeAlias = "拒收指派", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "拒收指派", notes = "拒收指派")
    @PutMapping("/refuseAssignment")
    public Result<?> refuseAssignment(@RequestBody RefuseAssignmentDTO refuseAssignmentDTO) {
        faultService.refuseAssignment(refuseAssignmentDTO);
        return Result.OK("拒收指派成功");
    }

    /**
     * 开始维修
     * @param refuseAssignmentDTO
     * @return
     */
    @AutoLog(value = "开始维修", operateType = 3, operateTypeAlias = "开始维修", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "开始维修", notes = "开始维修")
    @PutMapping("/startRepair")
    public Result<?> startRepair(@RequestBody RefuseAssignmentDTO refuseAssignmentDTO) {
        faultService.startRepair(refuseAssignmentDTO.getFaultCode());
        return Result.OK("操作成功");
    }

    /**
     * 挂起
     * @param hangUpDTO
     * @return
     */
    @AutoLog(value = "挂起", operateType = 3, operateTypeAlias = "挂起", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "挂起", notes = "挂起")
    @PutMapping("/hangUp")
    public Result<?> hangUp(@RequestBody HangUpDTO hangUpDTO) {
        faultService.hangUp(hangUpDTO);
        return Result.OK("操作成功");
    }

    /**
     * 审批挂起
     * @param approvalHangUpDTO
     * @return
     */
    @AutoLog(value = "审批挂起", operateType = 3, operateTypeAlias = "审批挂起", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "审批挂起", notes = "审批挂起")
    @PutMapping("/approvalHangUp")
    public Result<?> approvalHangUp(@RequestBody ApprovalHangUpDTO approvalHangUpDTO) {
        faultService.approvalHangUp(approvalHangUpDTO);
        return Result.OK("操作成功");
    }

    /**
     * 取消挂起
     * @param hangUpDTO
     * @return
     */
    @AutoLog(value = "取消挂起", operateType = 3, operateTypeAlias = "审批挂起", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "取消挂起", notes = "取消挂起")
    @PutMapping("/cancelHangup")
    public Result<?> cancelHangup(@RequestBody HangUpDTO hangUpDTO) {
        faultService.cancelHangup(hangUpDTO.getFaultCode());
        return Result.OK("操作成功");
    }

    /**
     * 查询填写维修记录详情
     * @param faultCode 故障指派
     * @return
     */
    @AutoLog(value = "查询填写维修记录详情")
    @ApiOperation(value = "查询填写维修记录详情", notes = "查询填写维修记录详情")
    @GetMapping("/queryRepairRecord")
    @ApiResponses({
            @ApiResponse(code = 200, response = RepairRecordDTO.class, message = "成功")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "faultCode", value = "故障编码", required = true, paramType = "query")
    })
    public Result<RepairRecordDTO> queryRepairRecord(@RequestParam(value = "faultCode") String faultCode) {
        RepairRecordDTO repairRecordDTO =  faultService.queryRepairRecord(faultCode);
        return Result.OK(repairRecordDTO);
    }

    /**
     * 填写维修记录
     * @param repairRecordDTO
     * @return
     */
    @AutoLog(value = "填写维修记录")
    @ApiOperation(value = "填写维修记录", notes = "填写维修记录")
    @PutMapping("/updateRepairRecord")
    public Result<?> updateRepairRecord(@RequestBody RepairRecordDTO repairRecordDTO) {
        faultService.fillRepairRecord(repairRecordDTO);
        return Result.OK("操作成功");
    }
    /**
     *  已驳回-保存
     * @param fault
     * @return
     */
    @AutoLog(value = "已驳回-保存")
    @ApiOperation(value = "已驳回-保存", notes = "已驳回-保存")
    @PutMapping("/saveResult")
    public Result<?> saveResult(@RequestBody Fault fault) {
        faultService.saveResult(fault);
        return Result.OK("操作成功");
    }

    /**
     *  故障上报驳回-再次提交审核
     * @param faultCode
     * @return
     */
    @AutoLog(value = "故障提交审核", operateType =  3, operateTypeAlias = "提审",  permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "故障上报驳回-再次提交审核", notes = "故障上报驳回-再次提交审核")
    @PutMapping("/submitResult")
    public Result<?> submitResult(@RequestParam  String faultCode) {
        faultService.submitResult(faultCode);
        return Result.OK("操作成功");
    }
    /**
     *  维修结果审核
     * @param resultDTO
     * @return
     */
    @AutoLog(value = "维修结果审核")
    @ApiOperation(value = "维修结果审核", notes = "维修结果审核")
    @PutMapping("/approvalResult")
    public Result<?> approvalResult(@RequestBody ApprovalResultDTO resultDTO) {
        faultService.approvalResult(resultDTO);
        return Result.OK("操作成功");
    }

    /**
     * 解决方案， 推荐
     * @param
     * @return
     */
    @AutoLog(value = "解决方案推荐查询")
    @ApiOperation(value = "解决方案推荐查询", notes = "解决方案推荐查询")
    @GetMapping("/queryKnowledge")
    public Result<KnowledgeDTO> queryKnowledge(FaultKnowledgeBase faultKnowledgeBase) {
        KnowledgeDTO knowledgeDTO = faultService.queryKnowledge(faultKnowledgeBase);
        return Result.OK(knowledgeDTO);
    }

    /**
     * 故障解决方案分页列表查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "故障解决方案分页列表查询")
    @ApiOperation(value = "故障解决方案分页列表查询", notes = "故障解决方案分页列表查询")
    @GetMapping(value = "/KnowledgeList")
    public Result<IPage<FaultKnowledgeBase>> queryKnowledgePageList(FaultKnowledgeBase knowledgeBase,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        Page<FaultKnowledgeBase> page = new Page<>(pageNo, pageSize);
        IPage<FaultKnowledgeBase> pageList = faultService.pageList(page,knowledgeBase);
        return Result.OK(pageList);
    }



    @AutoLog(value = "查询工作类型")
    @ApiOperation(value = "查询工作类型", notes = "查询工作类型")
    @GetMapping("/queryCsWork")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "faultCode", value = "故障编码", required = true, paramType = "query")
    })
    public Result<List<CsWork>> queryCsWork(@Param(value = "faultCode") String faultCode) {
        List<CsWork> list = faultService.queryCsWork(faultCode);

        return Result.OK(list);
    }


    @GetMapping("/sysUser/queryUser")
    @ApiOperation("查询当前班组成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "faultCode", value = "故障编码", required = true, paramType = "query")
    })
    public Result<List<LoginUser>> queryUser(@Param(value = "faultCode") String faultCode) {

        List<LoginUser> list = faultService.queryUser(faultCode);

        return Result.OK(list);
    }


    @PutMapping("/confirmDevice")
    @ApiOperation("修改设备/确认设备")
    public Result<?> confirmDevice(@RequestBody ConfirmDeviceDTO confirmDeviceDTO) {
        faultService.confirmDevice(confirmDeviceDTO);
        return Result.OK("操作成功");
    }

    @PostMapping("/useKnowledgeBase")
    @ApiOperation("使用知识库")
    public Result<?> useKnowledgeBase(@RequestBody UseKnowledgeDTO useKnowledgeDTO) {
        faultService.useKnowledgeBase(useKnowledgeDTO.getFaultCode(), useKnowledgeDTO.getKnowledgeId());
        return Result.OK("操作成功");
    }

    @AutoLog(value = "查询", operateType =  1, operateTypeAlias = "查询", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "分页列表查询", notes = "fault-分页列表查询")
    @GetMapping(value = "/repairDeviceList")
    public Result<IPage<FaultDeviceRepairDTO>> queryPageList(FaultDeviceRepairDTO faultDeviceRepairDto,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        Page<FaultDeviceRepairDTO> page = new Page<FaultDeviceRepairDTO>(pageNo, pageSize);
        IPage<FaultDeviceRepairDTO> faultDeviceRepairDtoList = faultDeviceService.queryRepairDeviceList(page, faultDeviceRepairDto);
        return Result.OK(faultDeviceRepairDtoList);
    }

    @AutoLog(value = "设备返修", operateType =  3, operateTypeAlias = "设备返修",  permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "设备返修", notes = "设备返修")
    @RequestMapping(value = "/repairDeviceEdit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody FaultDeviceRepairDTO faultDeviceRepairDTO) {
        FaultDevice faultDevice = new FaultDevice();
        BeanUtils.copyProperties(faultDeviceRepairDTO, faultDevice);
        faultDevice.setRepairStatus("2");
        faultDeviceService.updateById(faultDevice);
        return Result.OK("返修成功!");
    }

    @AutoLog(value = "设备验收", operateType =  3, operateTypeAlias = "设备验收",  permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "设备验收", notes = "设备验收")
    @RequestMapping(value = "/repairDeviceCheck", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> check(@RequestBody FaultDeviceRepairDTO faultDeviceRepairDTO) {
        FaultDevice faultDevice = new FaultDevice();
        BeanUtils.copyProperties(faultDeviceRepairDTO, faultDevice);
        faultDevice.setRepairStatus("3");
        faultDeviceService.updateById(faultDevice);
        return Result.OK("验收成功!");
    }



}
