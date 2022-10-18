package com.aiurt.modules.robot.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.robot.dto.TaskRepairInfoDTO;
import com.aiurt.modules.robot.entity.TaskRepairInfo;
import com.aiurt.modules.robot.service.ITaskRepairInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: task_repair_info
 * @Author: jeecg-boot
 * @Date: 2022-10-08
 * @Version: V1.0
 */
@Api(tags = "机器人巡检任务关联故障")
@RestController
@RequestMapping("/robot/taskRepairInfo")
@Slf4j
public class TaskRepairInfoController extends BaseController<TaskRepairInfo, ITaskRepairInfoService> {
    @Autowired
    private ITaskRepairInfoService taskRepairInfoService;
    /**
    @Autowired
    private IBdDeviceArchivesService bdDeviceArchivesService;
    */

    /**
     * @param taskRepairInfo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "task_repair_info-分页列表查询")
    @ApiOperation(value = "task_repair_info-分页列表查询", notes = "task_repair_info-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(TaskRepairInfo taskRepairInfo,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<TaskRepairInfo> queryWrapper = QueryGenerator.initQueryWrapper(taskRepairInfo, req.getParameterMap());
        Page<TaskRepairInfo> page = new Page<TaskRepairInfo>(pageNo, pageSize);
        IPage<TaskRepairInfo> pageList = taskRepairInfoService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "task_repair_info-通过id查询")
    @ApiOperation(value = "task_repair_info-通过id查询", notes = "task_repair_info-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        TaskRepairInfo taskRepairInfo = taskRepairInfoService.getById(id);
        if (taskRepairInfo == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(taskRepairInfo);
    }

    /**
     * 巡检记录-生成维修单
     *
     * @return
     */
    @AutoLog(value = "巡检记录-生成维修单")
    @ApiOperation(value = "巡检记录-生成维修单", notes = "巡检记录-生成维修单")
    @PostMapping(value = "/add")
    public Result<?> add(@Validated @RequestBody TaskRepairInfoDTO taskRepairInfoDTO) {
        taskRepairInfoService.add(taskRepairInfoDTO);
        return Result.OK("故障上报成功！");
    }

    /**
     * 通过taskId查询维修单
     *
     * @param taskId
     * @return
     */
    @AutoLog(value = "通过taskId查询维修单")
    @ApiOperation(value = "通过taskId查询维修单", notes = "通过taskId查询维修单")
    @GetMapping(value = "/queryByTaskId")
    public Result<List<TaskRepairInfo>> queryByTaskId(@RequestParam(name = "taskId", required = true) String taskId) {
        List<TaskRepairInfo> taskRepairInfos = taskRepairInfoService.queryByTaskId(taskId);
        return Result.OK(taskRepairInfos);
    }

    /**
     * 机器人设备报修查询对应任务下的设备
     *
     * @param queryDevice4ReapirDTO
     * @return
     */
    /*
    @AutoLog(value = "机器人设备报修查询对应任务下的设备")
    @ApiOperation(value = "机器人设备报修查询对应任务下的设备", notes = "机器人设备报修查询对应任务下的设备")
    @PostMapping(value = "/getDeviceByTaskId")
    public Result<IPage<BdDeviceArchives>> getDeviceByTaskId(@RequestBody QueryDevice4ReapirDTO queryDevice4ReapirDTO) {
        if (CollectionUtil.isEmpty(queryDevice4ReapirDTO.getDeviceCodes())) {
            return Result.OK(new Page<>(queryDevice4ReapirDTO.getPageNo(), queryDevice4ReapirDTO.getPageSize()));
        }
        IPage<BdDeviceArchives> deviceByOtherOfRepairTeam = bdDeviceArchivesService.getDeviceByOtherOfRepairTeam(queryDevice4ReapirDTO);
        return Result.OK(deviceByOtherOfRepairTeam);
    }
    */
}
