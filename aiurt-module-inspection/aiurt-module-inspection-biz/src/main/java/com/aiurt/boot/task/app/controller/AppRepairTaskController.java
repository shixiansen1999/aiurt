package com.aiurt.boot.task.app.controller;


import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.manager.dto.ExamineDTO;
import com.aiurt.boot.manager.dto.OrgDTO;
import com.aiurt.boot.task.dto.CheckListDTO;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.dto.WriteMonadDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.entity.RepairTaskDeviceRel;
import com.aiurt.boot.task.service.IRepairTaskDeviceRelService;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Description: app检修任务
 * @Author: aiurt
 * @Date: 2022-07-01
 * @Version: V1.0
 */
@Api(tags = "app检修任务")
@RestController
@RequestMapping("/task/app/appRepairTask")
@Slf4j
public class AppRepairTaskController extends BaseController<RepairTask, IRepairTaskService> {

    @Autowired
    private IRepairTaskService repairTaskService;

    @Autowired
    private IRepairTaskDeviceRelService repairTaskDeviceRelService;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;


    /**
     * app检修任务列表查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "app检修任务-检修任务列表查询", operateType = 1, operateTypeAlias = "检修任务列表", module = ModuleType.INSPECTION)
    @ApiOperation(value = "app检修任务-检修任务列表查询", notes = "app检修任务-检修任务列表查询")
    @GetMapping(value = "/appRepairTaskPageList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTask.class)
    })
    @PermissionData(appComponent = "Repair/TaskList/index")
    public Result<Page<RepairTask>> appRepairTaskPageList(RepairTask condition,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<RepairTask> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTask> repairTaskPage = repairTaskService.selectables(pageList, condition);
        return Result.OK(repairTaskPage);
    }


    /**
     * app检修工单详情
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "app检修任务-检修工单列表查询", operateType = 1, operateTypeAlias = "检修工单列表", module = ModuleType.INSPECTION)
    @ApiOperation(value = "app检修任务-检修工单列表", notes = "app检修任务-检修工单列表")
    @GetMapping(value = "/appRepairSelectTasklet")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTaskDTO.class)
    })
    public Result<Page<RepairTaskDTO>> appRepairSelectTasklet(RepairTaskDTO condition,
                                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<RepairTaskDTO> pageList = new Page<>(pageNo, pageSize);
        pageList = repairTaskService.selectTasklet(pageList, condition);
        return Result.OK(pageList);
    }

    /**
     * app检修工单详情
     *
     * @param deviceId
     * @return
     */
    @AutoLog(value = "app检修任务-检修工单详情查询", operateType = 1, operateTypeAlias = "检修工单详情", module = ModuleType.INSPECTION)
    @ApiOperation(value = "app检修任务-检修工单详情", notes = "app检修任务-检修工单详情")
    @GetMapping(value = "/appRepairSelectCheckList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = CheckListDTO.class)
    })
    public Result<CheckListDTO> appRepairSelectCheckList(@RequestParam(name = "deviceId", required = true) String deviceId,
                                                         @RequestParam(name = "overhaulCode", required = false) String overhaulCode
    ) {
        CheckListDTO checkListDTO = repairTaskService.selectCheckList(deviceId, overhaulCode);
        return Result.OK(checkListDTO);
    }

    /**
     * app检修任务-待执行-执行
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "app检修任务-待执行-执行")
    @ApiOperation(value = "app检修任务-待执行-执行", notes = "app检修任务-待执行-执行")
    @PostMapping(value = "/appRepairToBeImplement")
    public Result<String> appRepairToBeImplement(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.toBeImplement(examineDTO);
        return Result.OK("执行成功！");
    }


    /**
     * app检修任务-执行中-执行-提交
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "app检修任务-执行中-执行-提交")
    @ApiOperation(value = "app检修任务-执行中-执行-提交", notes = "app检修任务-执行中-执行-提交")
    @PostMapping(value = "/appRepairInExecution")
    public Result<String> appRepairInExecution(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.inExecution(examineDTO);
        return Result.OK("执行成功！");
    }

    /**
     * app检修任务-审核
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "app检修任务-审核")
    @ApiOperation(value = "app检修任务-审核", notes = "app检修任务-审核")
    @PostMapping(value = "/appRepairToExamine")
    public Result<String> appRepairToExamine(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.toExamine(examineDTO);
        return Result.OK("操作成功！");
    }


    /**
     * app检修任务_退回
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "app检修任务-退回")
    @ApiOperation(value = "app检修任务-退回", notes = "app检修任务-退回")
    @PostMapping(value = "/appConfirmedDelete")
    public Result<String> appConfirmedDelete(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.confirmedDelete(examineDTO);
        return Result.OK("退回成功！");
    }

    /**
     * app检修任务-验收
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "app检修任务-验收")
    @ApiOperation(value = "app检修任务-验收", notes = "app检修任务-验收")
    @PostMapping(value = "/appRepairAcceptance")
    public Result<String> appRepairAcceptance(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.acceptance(examineDTO);
        return Result.OK("操作成功！");
    }

    /**
     * 领取检修任务
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修管理-检修计划-领取检修任务", operateType = 2, operateTypeAlias = "领取检修任务", module = ModuleType.INSPECTION)
    @ApiOperation(value = "领取检修任务", notes = "领取检修任务")
    @GetMapping(value = "/receiveTask")
    public Result<?> receiveTask(@RequestParam @ApiParam(value = "检修计划id", name = "id", required = true) String id) {
        repairTaskService.receiveTask(id);
        return Result.OK("领取检修任务成功！");
    }

    /**
     * 填写检修工单
     *
     * @return
     */
    @AutoLog(value = "检修管理-检修任务管理-填写检修工单", operateType = 3, operateTypeAlias = "填写检修工单", module = ModuleType.INSPECTION)
    @ApiOperation(value = "填写检修工单", notes = "填写检修工单")
    @PostMapping(value = "/writeMonad")
    public Result<?> writeMonad(@RequestBody WriteMonadDTO monadDTO) {
        repairTaskService.writeMonad(monadDTO);
        return Result.OK("填写成功");
    }

    /**
     * 填写检修单上的同行人
     *
     * @param code   检修单code
     * @param peerId 同行人ids
     */
    @AutoLog(value = "填写检修单上的同行人")
    @ApiOperation(value = "填写检修单上的同行人", notes = "填写检修单上的同行人")
    @PostMapping(value = "/writePeerPeople")
    public Result<?> writePeerPeople(@RequestParam @ApiParam(value = "检修单code", name = "code", required = true) String code,
                                     @RequestParam @ApiParam(value = "同行人，多个用英文逗号隔开", name = "peerId", required = true) String peerId) {
        repairTaskService.writePeerPeople(code, peerId);
        return Result.OK("填写成功");
    }

    /**
     * 填写检修单上的检修位置
     *
     * @param id               检修单id
     * @param specificLocation 检修位置
     * @return
     */
    @AutoLog(value = "填写检修单上的检修位置")
    @ApiOperation(value = "填写检修单上的检修位置", notes = "填写检修单上的检修位置")
    @PostMapping(value = "/writeLocation")
    public Result<?> writeLocation(@RequestParam @ApiParam(value = "检修单id", name = "id", required = true) String id,
                                   @RequestParam @ApiParam(value = "检修位置", name = "specificLocation", required = true) String specificLocation) {
        repairTaskService.writeLocation(id, specificLocation);
        return Result.OK("填写成功");
    }

    /**
     * 提交检修工单
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修管理-检修任务管理-提交检修工单", operateType = 3, operateTypeAlias = "提交检修工单", module = ModuleType.INSPECTION)
    @ApiOperation(value = "提交检修工单", notes = "提交检修工单")
    @PostMapping(value = "/submitMonad")
    public Result<?> submitMonad(@RequestParam @ApiParam(value = "检修单id", name = "id", required = true) String id) {
        repairTaskService.submitMonad(id);
        return Result.OK("提交成功");
    }

    /**
     * 检修单同行人下拉
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修单同行人下拉")
    @ApiOperation(value = "检修单同行人下拉", notes = "检修单同行人下拉")
    @GetMapping(value = "/peerDropDown")
    public Result<List<OrgDTO>> queryPeerList(@RequestParam @ApiParam(value = "检修单id", name = "id", required = true) String id) {
        List<OrgDTO> orgDTOList = repairTaskService.queryPeerList(id);
        return Result.OK(orgDTOList);
    }

    /**
     * 确认检修任务
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "检修管理-检修任务管理-确认检修任务", operateType = 3, operateTypeAlias = "确认检修任务", module = ModuleType.INSPECTION)
    @ApiOperation(value = "确认检修任务", notes = "确认检修任务")
    @PostMapping(value = "/confirmTask")
    public Result<String> confirmTask(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.confirmTask(examineDTO);
        return Result.OK("已确认");
    }

    /**
     * 扫码设备查询检修单
     *
     * @param taskId     检修任务id
     * @param deviceCode 设备编码
     * @return
     */
    @AutoLog(value = "检修管理-检修任务管理-扫码设备查询检修单", operateType = 1, operateTypeAlias = "扫码设备查询检修单", module = ModuleType.INSPECTION)
    @ApiOperation(value = "扫码设备查询检修单", notes = "扫码设备查询检修单")
    @GetMapping(value = "/scanCodeDevice")
    public Result<List<RepairTaskDeviceRel>> scanCodeDevice(@RequestParam @ApiParam(name = "taskId", required = true, value = "检修任务id") String taskId,
                                                            @RequestParam @ApiParam(name = "deviceCode", required = true, value = "设备编码") String deviceCode) {
        List<RepairTaskDeviceRel> repairTaskDeviceRels = repairTaskService.scanCodeDevice(taskId, deviceCode);
        if (repairTaskDeviceRels == null) {
            return Result.error("未匹配到检修单");
        }
        return Result.OK(repairTaskDeviceRels);
    }


    /**
     * 检修
     *
     * @param repairTaskDeviceRel
     * @return
     */
    @AutoLog(value = "app检修任务-检修")
    @ApiOperation(value = "app检修任务-检修", notes = "app检修任务-检修")
    @PostMapping(value = "/overhaul")
    public Result<String> edit(@RequestBody RepairTaskDeviceRel repairTaskDeviceRel) {
        RepairTaskDeviceRel repairTaskDeviceRel1 = repairTaskDeviceRelService.getById(repairTaskDeviceRel.getId());
        // 同步更新检修任务的开始时间
        if (ObjectUtil.isNotEmpty(repairTaskDeviceRel1)) {
            RepairTask repairTask = repairTaskService.getBaseMapper().selectById(repairTaskDeviceRel1.getRepairTaskId());
            if (ObjectUtil.isNotEmpty(repairTask) && ObjectUtil.isEmpty(repairTask.getBeginTime())) {
                repairTask.setBeginTime(new Date());
                repairTaskService.getBaseMapper().updateById(repairTask);
            }
        }

        // 更新检修单上的开始时间
        if (repairTaskDeviceRel1.getStartTime() != null) {
            return Result.OK("检修已开始!");
        } else {
            repairTaskDeviceRel.setStartTime(new Date());
            repairTaskDeviceRelService.updateById(repairTaskDeviceRel);
            return Result.OK("成功!");
        }
    }



    /**
     * app审核检修任务列表查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "app审核检修任务列表查询", operateType = 1, operateTypeAlias = "检修任务列表", module = ModuleType.INSPECTION)
    @ApiOperation(value = "app审核检修任务列表查询", notes = "app审核检修任务列表查询")
    @GetMapping(value = "/appRepairTaskConfirmList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTask.class)
    })
    @PermissionData(appComponent = "Repair/AduitList/index")
    public Result<Page<RepairTask>> appRepairTaskConfirmList(RepairTask condition,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<RepairTask> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTask> repairTaskPage = repairTaskService.selectables(pageList, condition);
        return Result.OK(repairTaskPage);
    }


    @AutoLog(value = "app检修任务-检修单无设备的检修位置", operateType = 1, operateTypeAlias = "app检修任务-检修单无设备的检修位置", module = ModuleType.INSPECTION)
    @ApiOperation(value = "app检修任务-检修单无设备的检修位置", notes = "app检修任务-检修单无设备的检修位置")
    @GetMapping(value = "/getPositionCodeByStationCode")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = CsStationPosition.class)
    })
    public Result<CsStation> getPositionCodeByStationCode(@RequestParam @ApiParam(name = "stationCode", required = true, value = "检修单站点编号") String stationCode){
        CsStation positionCodeByStationCode = iSysBaseAPI.getPositionCodeByStationCode(stationCode);
        return Result.OK(positionCodeByStationCode);
    }

    /**
     * app验收检修任务列表查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "app验收检修任务列表查询", operateType = 1, operateTypeAlias = "检修任务列表", module = ModuleType.INSPECTION)
    @ApiOperation(value = "app验收检修任务列表查询", notes = "app验收检修任务列表查询")
    @GetMapping(value = "/appRepairTaskReceiptList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTask.class)
    })
    @PermissionData(appComponent = "Repair/AduitList/index")
    public Result<Page<RepairTask>> appRepairTaskReceiptList(RepairTask condition,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<RepairTask> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTask> repairTaskPage = repairTaskService.selectables(pageList, condition);
        return Result.OK(repairTaskPage);
    }

}
