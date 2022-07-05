package com.aiurt.boot.task.app.controller;


import com.aiurt.boot.manager.dto.ExamineDTO;
import com.aiurt.boot.task.dto.CheckListDTO;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: app检修任务
 * @Author: aiurt
 * @Date:   2022-07-01
 * @Version: V1.0
 */
@Api(tags="app检修任务")
@RestController
@RequestMapping("/task/app/appRepairTask")
@Slf4j
public class AppRepairTaskController extends BaseController<RepairTask, IRepairTaskService> {

    @Autowired
    private IRepairTaskService repairTaskService;


    /**
     * app检修任务列表查询
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "app检修任务-检修任务列表查询")
    @ApiOperation(value="app检修任务-检修任务列表查询", notes="app检修任务-检修任务列表查询")
    @GetMapping(value = "/appRepairTaskPageList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTask.class)
    })
    public Result<Page<RepairTask>> appRepairTaskPageList(RepairTask condition,
                                                       @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                       @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
    ){
        Page<RepairTask> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTask> repairTaskPage = repairTaskService.selectables(pageList, condition);
        return Result.OK(repairTaskPage);
    }


    /**
     * app检修工单详情
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "app检修任务-检修工单列表")
    @ApiOperation(value="app检修任务-检修工单列表", notes="app检修任务-检修工单列表")
    @GetMapping(value = "/appRepairSelectTasklet")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RepairTaskDTO.class)
    })
    public Result<Page<RepairTaskDTO>> appRepairSelectTasklet( RepairTaskDTO condition,
                                                            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
    ){
        Page<RepairTaskDTO> pageList = new Page<>(pageNo, pageSize);
        Page<RepairTaskDTO> repairTaskPage = repairTaskService.selectTasklet(pageList, condition);
        return Result.OK(repairTaskPage);
    }

    /**
     * app检修工单详情
     * @param deviceId
     * @return
     */
    @AutoLog(value = "app检修任务-检修工单详情")
    @ApiOperation(value="app检修任务-检修工单详情", notes="app检修任务-检修工单详情")
    @GetMapping(value = "/appRepairSelectCheckList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = CheckListDTO.class)
    })
    public Result<CheckListDTO> appRepairSelectCheckList(@RequestParam(name="deviceId",required=true) String deviceId,
                                                @RequestParam(name="overhaulCode",required=true) String overhaulCode
    ){
        CheckListDTO checkListDTO = repairTaskService.selectCheckList(deviceId,overhaulCode);
        return Result.OK(checkListDTO);
    }

    /**
      *   待执行-执行
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "检修任务-待执行-执行")
    @ApiOperation(value="检修任务-待执行-执行", notes="检修任务-待执行-执行")
    @PostMapping(value = "/toBeImplement")
    public Result<String> toBeImplement(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.toBeImplement(examineDTO);
        return Result.OK("执行成功！");
    }



    /**
     *   执行中-执行-提交
     *
     * @param examineDTO
     * @return
     */
    @AutoLog(value = "检修任务-执行中-执行-提交")
    @ApiOperation(value="检修任务-执行中-执行-提交", notes="检修任务-执行中-执行-提交")
    @PostMapping(value = "/inExecution")
    public Result<String> inExecution(@RequestBody ExamineDTO examineDTO) {
        repairTaskService.inExecution(examineDTO);
        return Result.OK("执行成功！");
    }


}
