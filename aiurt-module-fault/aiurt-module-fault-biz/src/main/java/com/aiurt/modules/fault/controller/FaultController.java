package com.aiurt.modules.fault.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.LimitSubmit;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.base.PageOrderGenerator;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.mapper.FaultMapper;
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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
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
    private FaultMapper faultMapper;

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

    @Autowired
    private ISysParamAPI iSysParamAPI;
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

        return Result.OK(pageList);
    }

    private void dealResult(List<Fault> records) {

        if (CollUtil.isEmpty(records)) {
            return;
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 查询故障设备列表
        Map<String, List<FaultDevice>> faultDeviceMap = faultDeviceService.queryListByFaultCodeList(records.stream().map(Fault::getCode).collect(Collectors.toList()))
                .stream().collect(Collectors.groupingBy(FaultDevice::getFaultCode));
        Map<String, Integer> weightMap = new HashMap<>();
        List<String> faultLevelList = records.stream().map(Fault::getFaultLevel).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollUtil.isNotEmpty(faultLevelList)) {
           weightMap = faultLevelService.getBaseMapper().selectList(Wrappers.lambdaQuery(FaultLevel.class)
                    .in(FaultLevel::getCode, faultLevelList))
                    .stream().collect(Collectors.toMap(FaultLevel::getCode, faultLevel -> {
                        try {
                            return Integer.parseInt(faultLevel.getWeight());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }));
        }


        Map<String, FaultAnalysisReport> reportMap = faultAnalysisReportService.getBaseMapper().selectList(Wrappers.lambdaQuery(FaultAnalysisReport.class)
                .in(FaultAnalysisReport::getFaultCode, records.stream().map(Fault::getCode).distinct().collect(Collectors.toList()))
                .eq(FaultAnalysisReport::getDelFlag, 0))
                .stream().collect(Collectors.toMap(FaultAnalysisReport::getFaultCode, Function.identity()));

        Map<String, Integer> finalWeightMap = weightMap;
        // 根据配置决定控制中心成员能否领取正线站点故障，开启时表示不能领取
        SysParamModel faultCenterReceiveParam = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_CENTER_RECEIVE);
        boolean faultCenterReceive = FaultConstant.ENABLE.equals(faultCenterReceiveParam.getValue());
        // 根据配置获取控制中心班组code,并判断当前登陆人所在班组是否是控制中心班组
        SysParamModel faultCenterAddOrg = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_CENTER_ADD_ORG);
        boolean contains1 = StrUtil.splitTrim(faultCenterAddOrg.getValue(),',').contains(user.getOrgCode());
        records.parallelStream().forEach(fault1 -> {
            // 通过站点获取工区部门
            List<String> departs = sysBaseAPI.getWorkAreaByCode(fault1.getStationCode())
                    .stream()
                    .flatMap(csWorkAreaModel -> csWorkAreaModel.getOrgCodeList().stream())
                    .collect(Collectors.toList());
            boolean contains2 = !(ObjectUtil.isNotEmpty(departs) && departs.contains(user.getOrgCode()));
            // 设置是否能领取
            if (faultCenterReceive && contains1 && contains2) {
                fault1.setCanReceive(false);
            } else {
                fault1.setCanReceive(true);
            }

            if(StrUtil.equalsIgnoreCase(user.getUsername(), fault1.getAppointUserName())){
                fault1.setIsFault(true);
            }else {
                fault1.setIsFault(false);
            }

            // 权重登记
            if (StrUtil.isNotBlank(fault1.getFaultLevel())) {
                fault1.setWeight(finalWeightMap.get(fault1.getFaultLevel()));
            }else {
                fault1.setWeight(0);
            }

            // 是否重新指派
            LambdaQueryWrapper<FaultRepairRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(FaultRepairRecord::getFaultCode, fault1.getCode());
            Long count = faultRepairRecordService.getBaseMapper().selectCount(lambdaQueryWrapper);
            fault1.setSignAgainFlag(count>0?1:0);


            //如果存在故障分析则返回true
            if (ObjectUtil.isNotNull(reportMap.get(fault1.getCode()))) {
                fault1.setIsFaultAnalysisReport(true);
            }

            List<FaultDevice> faultDeviceList = faultDeviceMap.get(fault1.getCode());
            fault1.setFaultDeviceList(faultDeviceList);
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
    @LimitSubmit(key = "add:#fault")
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
    @LimitSubmit(key = "approval:#approvalDTO")
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
    @LimitSubmit(key = "startRepair:#refuseAssignmentDTO")
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
    @LimitSubmit(key = "approvalResult:#resultDTO")
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
        // 根据故障编号获取故障所属组织机构
        Fault fault = faultService.lambdaQuery().eq(Fault::getCode, faultCode).last("limit 1").one();
        if (ObjectUtil.isEmpty(fault)) {
            return Result.OK(Collections.emptyList());
        } else {
            List<LoginUser> list = faultService.queryUser(fault);
            return Result.OK(list);
        }
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

    @AutoLog(value = "故障钻取", operateType =  1, operateTypeAlias = "故障钻取", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "故障钻取", notes = "故障钻取")
    @GetMapping(value = "/getHitchDrilling")
    public List<HitchDrillingDTO> getHitchDrilling(){
        List<HitchDrillingDTO> hitchDrillingDTOList = new ArrayList<>();
        LambdaQueryWrapper<Fault> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        ArrayList<Integer> integers = CollectionUtil.newArrayList(5, 6, 7);
        lambdaQueryWrapper.in(Fault::getStatus,integers);
        List<Fault> list1 = faultService.list(lambdaQueryWrapper);
        if (CollectionUtil.isEmpty(list1)){
            return hitchDrillingDTOList;
        }else {
            list1.forEach(e->{
                HitchDrillingDTO hitchDrillingDTO = new HitchDrillingDTO();
                String lineCode = e.getLineCode();
                if (StrUtil.isNotBlank(lineCode)){
                    String position = sysBaseAPI.getPosition(lineCode);
                    hitchDrillingDTO.setLine(position);
                }if (StrUtil.isNotBlank(e.getSymptoms())){
                    hitchDrillingDTO.setGzyy(e.getSymptoms());
                }if (ObjectUtil.isNotNull(e.getHappenTime())){
                    hitchDrillingDTO.setGztime(e.getHappenTime());
                }
                    hitchDrillingDTO.setGzstate("解决中");

                hitchDrillingDTOList.add(hitchDrillingDTO);
            });
        }
        return hitchDrillingDTOList;
    }

    /**
     * 填写维修记录时，参与人的查询，主要是为了过滤掉维修人，使维修人不能又是维修人
     * @param loginUser
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "查询用户", operateType =  1, operateTypeAlias = "查询用户", permissionUrl = PERMISSION_URL)
    @ApiOperation(value = "查询用户", notes = "查询用户")
    @GetMapping("/queryUser")
    public Result<?> queryUser(LoginUser loginUser, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(name = "faultCode") String faultCode,HttpServletRequest req){
        Fault fault = faultMapper.selectByCode(faultCode);
        if (Objects.isNull(fault)) {
            throw new AiurtBootException("故障工单不存在");
        }
        String userId = sysBaseAPI.getUserByUserName(fault.getAppointUserName());
        JSONObject jsonObject = sysBaseAPI.queryPageUserList(loginUser, Collections.singletonList(userId),
                "1", "0", pageNo, pageSize, req);
        return Result.ok(jsonObject);
    }

}
