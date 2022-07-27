package com.aiurt.modules.fault.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.constant.enums.OperateTypeEnum;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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


    @Autowired
    private IFaultService faultService;

    @Autowired
    private IFaultDeviceService faultDeviceService;

    /**
     * 分页列表查询
     *
     * @param fault
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "故障管理-故障列表-查询", operateType =  1, operateTypeAlias = "查询", module = ModuleType.FAULT)
    @ApiOperation(value = "分页列表查询", notes = "fault-分页列表查询")
    @GetMapping(value = "/list")
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
        QueryWrapper<Fault> queryWrapper = QueryGenerator.initQueryWrapper(fault, req.getParameterMap());
        Page<Fault> page = new Page<>(pageNo, pageSize);
        //修改查询条件
        queryWrapper.apply(StrUtil.isNotBlank(stationCode), "(line_code = {0} or station_code = {0} or station_position_code = {0})", stationCode);
        queryWrapper.apply(StrUtil.isNotBlank(fault.getDevicesIds()), "(code in (select fault_code from fault_device where device_code like  concat('%', {0}, '%')))", fault.getDevicesIds());
        queryWrapper.apply(StrUtil.isNotBlank(faultPhenomenon), "(fault_phenomenon like concat('%', {0}, '%') or code like  concat('%', {0}, '%'))", faultPhenomenon);
        queryWrapper.orderByDesc("create_time");
        IPage<Fault> pageList = faultService.page(page, queryWrapper);

        List<Fault> records = pageList.getRecords();
        records.stream().forEach(fault1 -> {
            List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(fault1.getCode());
            fault1.setFaultDeviceList(faultDeviceList);
        });

        // todo 优化排序
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param fault
     * @return
     */
    @AutoLog(value = "故障管理-故障列表-新增故障上报", operateType =  2, operateTypeAlias = "故障上报", module = ModuleType.FAULT)
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
    @AutoLog(value = "故障管理-故障列表-上报审批", operateType =  3, operateTypeAlias = "上报审批", module = ModuleType.FAULT)
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
    @AutoLog(value = "故障管理-故障列表-编辑", operateType =  3, operateTypeAlias = "编辑故障单", module = ModuleType.FAULT)
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
    @AutoLog(value = "故障管理-故障列表-作废", operateType =  3, operateTypeAlias = "作废故障单", module = ModuleType.FAULT)
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
    @AutoLog(value = "故障管理-故障列表-详情", operateType =  3, operateTypeAlias = "查看故障详情", module = ModuleType.FAULT)
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
    @AutoLog(value = "故障管理-故障列表-指派", operateType =  3, operateTypeAlias = "故障指派", module = ModuleType.FAULT)
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
    @AutoLog(value = "领取故障工单")
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
    @AutoLog(value = "接收指派")
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
    @AutoLog(value = "拒收指派")
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
    @AutoLog(value = "开始维修")
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
    @AutoLog(value = "挂起")
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
    @AutoLog(value = "审批挂起")
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
    @AutoLog(value = "取消挂起")
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
    private Result<?> useKnowledgeBase(@RequestBody UseKnowledgeDTO useKnowledgeDTO) {
        faultService.useKnowledgeBase(useKnowledgeDTO.getFaultCode(), useKnowledgeDTO.getKnowledgeId());
        return Result.OK("操作成功");
    }


}
