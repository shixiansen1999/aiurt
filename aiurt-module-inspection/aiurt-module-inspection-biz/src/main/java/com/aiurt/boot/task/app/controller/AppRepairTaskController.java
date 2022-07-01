package com.aiurt.boot.task.app.controller;


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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * app检修任务清单查询
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "app检修任务-检修任务清单查询")
    @ApiOperation(value="app检修任务-检修任务清单查询", notes="app检修任务-检修任务清单查询")
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


}
