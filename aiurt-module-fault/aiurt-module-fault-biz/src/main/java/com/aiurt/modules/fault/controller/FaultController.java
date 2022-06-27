package com.aiurt.modules.fault.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 分页列表查询
     *
     * @param fault
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "故障分页列表查询")
    @ApiOperation(value = "分页列表查询", notes = "fault-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Fault>> queryPageList(Fault fault,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        QueryWrapper<Fault> queryWrapper = QueryGenerator.initQueryWrapper(fault, req.getParameterMap());
        Page<Fault> page = new Page<>(pageNo, pageSize);
        IPage<Fault> pageList = faultService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param fault
     * @return
     */
    @AutoLog(value = "故障上报")
    @ApiOperation(value = "故障上报", notes = "故障上报")
    @PostMapping(value = "/add")
    public Result<?> add(@Validated @RequestBody Fault fault) {
        faultService.add(fault);
        return Result.OK("添加成功！");
    }

    /**
     * 审批
     *
     * @param approvalDTO
     * @return
     */
    @PutMapping("/approval")
    @ApiOperation(value = "故障审批", notes = "故障审批")
    @AutoLog("故障审批")
    public Result<?> approval(@RequestBody ApprovalDTO approvalDTO) {

        faultService.approval(approvalDTO);

        return Result.OK();
    }

    /**
     * 编辑
     *
     * @param fault
     * @return
     */
    @AutoLog(value = "编辑故障单")
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
    @AutoLog(value = "故障作废")
    @ApiOperation(value = "故障作废", notes = "故障作废")
    @PutMapping(value = "/cancel")
    public Result<String> cancel(@RequestBody CancelDTO cancelDTO) {
        faultService.cancel(cancelDTO);
        return Result.OK("作废成功!");
    }

    /**
     * 通过id查询
     *
     * @param code
     * @return
     */
    @AutoLog(value = "查看故障详情")
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

    @AutoLog(value = "故障指派")
    @ApiOperation(value = "故障指派", notes = "故障指派")
    @PutMapping("/assign")
    public Result<?> assign(@RequestBody AssignDTO assignDTO) {
        faultService.assign(assignDTO);
        return Result.OK("故障指派成功！");
    }

    @AutoLog(value = "领取故障工单")
    @ApiOperation(value = "领取故障工单", notes = "领取故障工单")
    @PutMapping("/receive")
    public Result<?> receive(@RequestBody AssignDTO assignDTO) {
        faultService.receive(assignDTO);
        return Result.OK("领取故障工单成功");
    }

    @AutoLog(value = "接收指派")
    @ApiOperation(value = "领取故障工单", notes = "领取故障工单")
    @PutMapping("/receiveAssignment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "故障编码", required = true, paramType = "query")
    })
    private Result<?> receiveAssignment(@RequestParam(name = "code") String code) {
        faultService.receiveAssignment(code);
        return Result.OK("领取故障工单成功。");
    }

    @AutoLog(value = "拒收指派")
    @ApiOperation(value = "拒收指派", notes = "拒收指派")
    @PutMapping("/refuseAssignment")
    private Result<?> refuseAssignment(@RequestBody RefuseAssignmentDTO refuseAssignmentDTO) {
        faultService.refuseAssignment(refuseAssignmentDTO);
        return Result.OK();
    }

    @AutoLog(value = "开始维修")
    @ApiOperation(value = "开始维修", notes = "开始维修")
    @PutMapping("/startRepair")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "faultCode", value = "故障编码", required = true, paramType = "query")
    })
    private Result<?> startRepair(@RequestParam(name = "faultCode") String faultCode) {
        faultService.startRepair(faultCode);
        return Result.OK();
    }


    @AutoLog(value = "挂起")
    @ApiOperation(value = "挂起", notes = "挂起")
    @PutMapping("/hangUp")
    private Result<?> hangUp(@RequestBody HangUpDTO hangUpDTO) {
        faultService.hangUp(hangUpDTO);
        return Result.OK();
    }

    @AutoLog(value = "审批挂起")
    @ApiOperation(value = "审批挂起", notes = "审批挂起")
    @PutMapping("/approvalHangUp")
    private Result<?> approvalHangUp(@RequestBody ApprovalHangUpDTO approvalHangUpDTO) {
        faultService.approvalHangUp(approvalHangUpDTO);
        return Result.OK();
    }

    @AutoLog(value = "取消挂起")
    @ApiOperation(value = "取消挂起", notes = "取消挂起")
    @PutMapping("/cancelHangup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "faultCode", value = "故障编码", required = true, paramType = "query")
    })
    private Result<?> cancelHangup(@RequestParam(name = "faultCode", required = true) String faultCode) {
        faultService.cancelHangup(faultCode);
        return Result.OK();
    }






}
