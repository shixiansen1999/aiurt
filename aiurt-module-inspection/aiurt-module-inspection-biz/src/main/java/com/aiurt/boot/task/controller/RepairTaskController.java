package com.aiurt.boot.task.controller;

import com.aiurt.boot.manager.dto.EquipmentOverhaulDTO;
import com.aiurt.boot.manager.dto.ExamineDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.task.dto.CheckListDTO;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.dto.RepairTaskStationDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.entity.RepairTaskEnclosure;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 检修任务
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Api(tags = "检修任务")
@RestController
@RequestMapping("/task/repairTask")
@Slf4j
public class RepairTaskController extends BaseController<RepairTask, IRepairTaskService> {
    @Autowired
    private IRepairTaskService repairTaskService;

    /**
     * 分页列表查询
     *
     * @param repairTask
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "repair_task-分页列表查询")
    @ApiOperation(value = "repair_task-分页列表查询", notes = "repair_task-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<RepairTask>> queryPageList(RepairTask repairTask,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        QueryWrapper<RepairTask> queryWrapper = QueryGenerator.initQueryWrapper(repairTask, req.getParameterMap());
        Page<RepairTask> page = new Page<RepairTask>(pageNo, pageSize);
        IPage<RepairTask> pageList = repairTaskService.page(page, queryWrapper);
        return Result.OK(pageList);
    }


    /**
     * 检修任务列表查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "检修任务-检修任务列表查询", operateType =  1, operateTypeAlias = "检修任务列表", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务-检修任务列表查询", notes = "检修任务-检修任务列表查询")
    @GetMapping(value = "/repairTaskPageList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTask.class)
    })
    @PermissionData(pageComponent="overhaul/RepairTaskList")
    public Result<Page<RepairTask>> repairTaskPageList(RepairTask condition,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<RepairTask> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTask> repairTaskPage = repairTaskService.selectables(pageList, condition);
        return Result.OK(repairTaskPage);
    }


    /**
     * 检修任务清单查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "检修任务-检修任务清单查询", operateType =  1, operateTypeAlias = "检修任务清单", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务-检修任务清单查询", notes = "检修任务-检修任务清单查询")
    @GetMapping(value = "/repairSelectTasklet")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTaskDTO.class)
    })
    public Result<Page<RepairTaskDTO>> repairSelectTasklet(RepairTaskDTO condition,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<RepairTaskDTO> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTaskDTO> repairTaskPage = repairTaskService.selectTasklet(pageList, condition);
        return Result.OK(repairTaskPage);
    }

    /**
     * 检修单信息下拉查询
     * @param taskId
     * @return
     */
    @AutoLog(value = "检修任务-检修单信息下拉查询", operateType =  1, operateTypeAlias = "检修单信息下拉查询", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务-检修单信息下拉查询", notes = "检修任务-检修单信息下拉查询")
    @GetMapping(value = "/repairSelectTaskList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTaskDTO.class)
    })
    public Result<List<RepairTaskDTO>> repairSelectTasklet(@ApiParam(name = "taskId", value = "任务id")
                                                               @RequestParam(value = "taskId", required = true)String taskId,
                                                           @ApiParam(name = "stationCode", value = "站点") @RequestParam(value="stationCode",required = true)String stationCode) {
        List<RepairTaskDTO> repairTaskPage = repairTaskService.selectTaskList(taskId,stationCode);
        return Result.OK(repairTaskPage);
    }

    /**
     * 站点信息下拉查询
     * @param taskId
     * @return
     */
    @AutoLog(value = "检修任务-站点信息下拉查询", operateType =  1, operateTypeAlias = "站点信息下拉查询", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务-站点信息下拉查询", notes = "检修任务-站点信息下拉查询")
    @GetMapping(value = "/repairTaskStationList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTaskDTO.class)
    })
    public Result<List<RepairTaskStationDTO>> repairTaskStationList(@ApiParam(name = "taskId", value = "任务id")
                                                                        @RequestParam(value = "taskId", required = true)String taskId) {
        List<RepairTaskStationDTO> repairTaskStations= repairTaskService.repairTaskStationList(taskId);
        return Result.OK(repairTaskStations);
    }

    @AutoLog(value = "检修任务-检修任务管理详情", operateType =  1, operateTypeAlias = "检修任务详情", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务-检修任务管理详情", notes = "检修任务-检修任务管理详情")
    @GetMapping(value = "/SelectRepairTaskInfo")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = CheckListDTO.class)
    })
    public Result<CheckListDTO> selectRepairTaskInfo(String taskId,
                                                           @ApiParam(name = "stationCode", value = "站点") @RequestParam(value="stationCode",required = true)String stationCode,
                                                           @ApiParam(name = "deviceId",value = "检修单")@RequestParam(value = "deviceId",required = true)String deviceId
    ) {
        CheckListDTO checkListDto = repairTaskService.selectRepairTaskInfo(taskId,stationCode,deviceId);
        return Result.OK(checkListDto);
    }



    /**
     * 检修任务清单查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "设备台账-检修履历")
    @ApiOperation(value = "设备台账-检修履历", notes = "设备台账-检修履历")
    @GetMapping(value = "/repairSelectTaskletForDevice")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTaskDTO.class)
    })
    public Result<Page<RepairTaskDTO>> repairSelectTaskletForDevice(RepairTaskDTO condition,
                                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<RepairTaskDTO> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTaskDTO> repairTaskPage = repairTaskService.repairSelectTaskletForDevice(pageList, condition);
        return Result.OK(repairTaskPage);
    }

    /**
     * 查看检修单附件
     *
     * @param resultId
     * @return
     */
    @AutoLog(value = "检修任务-检修结果附件查询", operateType =  1, operateTypeAlias = "检修结果附件", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务-检修结果附件查询", notes = "检修任务-检修结果附件查询")
    @GetMapping(value = "/selectEnclosure")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTaskEnclosure.class)
    })
    public Result<List<RepairTaskEnclosure>> selectEnclosure(@RequestParam(name = "resultId", required = true) String resultId
    ) {
        List<RepairTaskEnclosure> repairTaskEnclosures = repairTaskService.selectEnclosure(resultId);
        return Result.OK(repairTaskEnclosures);
    }

    /**
     * 专业和专业子系统下拉列表
     *
     * @param taskId
     * @return
     */
    @AutoLog(value = "检修任务-专业和专业子系统查询", operateType =  1, operateTypeAlias = "专业和专业子系统下拉列表", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务-专业和专业子系统查询", notes = "检修任务-专业和专业子系统下拉列表")
    @GetMapping(value = "/selectMajorCodeList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = MajorDTO.class)
    })
    public Result<List<MajorDTO>> selectMajorCodeList(@RequestParam(name = "taskId", required = true) String taskId
    ) {
        List<MajorDTO> majorDTOList = repairTaskService.selectMajorCodeList(taskId);
        return Result.OK(majorDTOList);
    }

    /**
     * 专业和专业子系统下拉列表
     *
     * @param taskId
     * @return
     */
    @AutoLog(value = "检修任务-设备类型和检修标准查询", operateType =  1, operateTypeAlias = "专业和专业子系统下拉列表", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务-设备类型和检修标准查询", notes = "检修任务-设备类型和检修标准下拉列表")
    @GetMapping(value = "/selectEquipmentOverhaulList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = EquipmentOverhaulDTO.class)
    })
    public Result<EquipmentOverhaulDTO> selectEquipmentOverhaulList(
            @RequestParam(name = "taskId", required = true) String taskId,
            @RequestParam(name = "majorCode", required = false) String majorCode,
            @RequestParam(name = "subsystemCode", required = false) String subsystemCode
    ) {
        EquipmentOverhaulDTO equipmentOverhaulDTO = repairTaskService.selectEquipmentOverhaulList(taskId, majorCode, subsystemCode);
        return Result.OK(equipmentOverhaulDTO);
    }

    /**
     * 检修单详情
     *
     * @param deviceId
     * @return
     */
    @AutoLog(value = "检修任务-检修单详情查询", operateType =  1, operateTypeAlias = "检修单详情", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务-检修单详情查询", notes = "检修任务-检修单详情")
    @GetMapping(value = "/selectCheckList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = CheckListDTO.class)
    })
    public Result<CheckListDTO> selectCheckList(@RequestParam(name = "deviceId", required = true) String deviceId,
                                                @RequestParam(name = "overhaulCode", required = false) String overhaulCode
    ) {
        CheckListDTO checkListDTO = repairTaskService.selectCheckList(deviceId, overhaulCode);
        return Result.OK(checkListDTO);
    }

    /**
     * 审核
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "检修任务-审核")
    @ApiOperation(value = "检修任务-审核", notes = "检修任务-审核")
    @PostMapping(value = "/toExamine")
    public Result<String> toExamine(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.toExamine(examineDTO);
        return Result.OK("审核成功！");
    }

    /**
     * 验收
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "检修任务-验收")
    @ApiOperation(value = "检修任务-验收", notes = "检修任务-验收")
    @PostMapping(value = "/acceptance")
    public Result<String> acceptance(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.acceptance(examineDTO);
        return Result.OK("验收成功！");
    }

    /**
     * 退回
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "检修任务-退回")
    @ApiOperation(value = "检修任务-退回", notes = "检修任务-退回")
    @PostMapping(value = "/confirmedDelete")
    public Result<String> confirmedDelete(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.confirmedDelete(examineDTO);
        return Result.OK("退回成功！");
    }

    /**
     * 添加
     *
     * @param repairTask
     * @return
     */
    @AutoLog(value = "repair_task-添加")
    @ApiOperation(value = "repair_task-添加", notes = "repair_task-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody RepairTask repairTask) {
        repairTaskService.save(repairTask);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param repairTask
     * @return
     */
    @AutoLog(value = "repair_task-编辑")
    @ApiOperation(value = "repair_task-编辑", notes = "repair_task-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody RepairTask repairTask) {
        repairTaskService.updateById(repairTask);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "repair_task-通过id删除")
    @ApiOperation(value = "repair_task-通过id删除", notes = "repair_task-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        repairTaskService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "repair_task-批量删除")
    @ApiOperation(value = "repair_task-批量删除", notes = "repair_task-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.repairTaskService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "repair_task-通过id查询", notes = "repair_task-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<RepairTask> queryById(@RequestParam(name = "id", required = true) String id) {
        RepairTask repairTask = repairTaskService.getById(id);
        if (repairTask == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(repairTask);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param repairTask
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairTask repairTask) {
        return super.exportXls(request, repairTask, RepairTask.class, "repair_task");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, RepairTask.class);
    }

    /**
     * 检修任务审核列表查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "检修任务审核列表查询", operateType =  1, operateTypeAlias = "检修任务审核列表查询", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务审核列表查询", notes = "检修任务审核列表查询")
    @GetMapping(value = "/repairTaskConfirmList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTask.class)
    })
    @PermissionData(pageComponent="overhaul/RepairTaskFinishAudit")
    public Result<Page<RepairTask>> repairTaskConfirmList(RepairTask condition,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<RepairTask> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTask> repairTaskPage = repairTaskService.selectables(pageList, condition);
        return Result.OK(repairTaskPage);
    }

    /**
     * 检修任务验收列表查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "检修任务验收列表查询", operateType =  1, operateTypeAlias = "检修任务验收列表查询", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修任务验收列表查询", notes = "检修任务验收列表查询")
    @GetMapping(value = "/repairTaskReceiptList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTask.class)
    })
    @PermissionData(pageComponent="overhaul/RepairTaskCheck")
    public Result<Page<RepairTask>> repairTaskReceiptList(RepairTask condition,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<RepairTask> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTask> repairTaskPage = repairTaskService.selectables(pageList, condition);
        return Result.OK(repairTaskPage);
    }

}
