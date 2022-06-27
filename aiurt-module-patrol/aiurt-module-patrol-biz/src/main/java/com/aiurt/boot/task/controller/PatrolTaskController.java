package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.dto.PatrolTaskUserDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Api(tags = "巡检任务")
@RestController
@RequestMapping("/patrolTask")
@Slf4j
public class PatrolTaskController extends BaseController<PatrolTask, IPatrolTaskService> {
    @Autowired
    private IPatrolTaskService patrolTaskService;
    @Autowired
    private IPatrolTaskDeviceService patrolTaskDeviceService;

    /**
     * 分页列表查询
     *
     * @param patrolTaskParam
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "patrol_task-分页列表查询")
    @ApiOperation(value = "PC巡检任务池列表-分页列表查询", notes = "PC巡检任务池列表-分页列表查询")
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<IPage<PatrolTaskParam>> queryPageList(PatrolTaskParam patrolTaskParam,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        HttpServletRequest req) {
        Page<PatrolTaskParam> page = new Page<PatrolTaskParam>(pageNo, pageSize);
        IPage<PatrolTaskParam> pageList = patrolTaskService.getTaskList(page, patrolTaskParam);
        return Result.OK(pageList);
    }

    @ApiOperation(value = "PC巡检任务池详情-基本信息", notes = "PC巡检任务池详情-基本信息")
    @RequestMapping(value = "/basicInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<PatrolTaskParam> selectBasicInfo(@RequestBody PatrolTaskParam patrolTaskParam,
                                                   HttpServletRequest req) {
        PatrolTaskParam task = patrolTaskService.selectBasicInfo(patrolTaskParam);
        return Result.OK(task);
    }

    @ApiOperation(value = "PC巡检任务池详情-巡检工单", notes = "PC巡检任务池详情-巡检工单")
    @RequestMapping(value = "/billInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> selectBillInfo(PatrolTaskDeviceParam patrolTaskDeviceParam,
                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                    HttpServletRequest req) {
        Page<PatrolTaskDeviceParam> page = new Page<>(pageNo, pageSize);
        IPage<PatrolTaskDeviceParam> taskDevicePageList = patrolTaskDeviceService.selectBillInfo(page,patrolTaskDeviceParam);
        return Result.OK(taskDevicePageList);
    }

    /**
     * 添加
     *
     * @param patrolTask
     * @return
     */
    @AutoLog(value = "patrol_task-添加")
    @ApiOperation(value = "patrol_task-添加", notes = "patrol_task-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody PatrolTask patrolTask) {
        patrolTaskService.save(patrolTask);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param patrolTask
     * @return
     */
    @AutoLog(value = "patrol_task-编辑")
    @ApiOperation(value = "patrol_task-编辑", notes = "patrol_task-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody PatrolTask patrolTask) {
        patrolTaskService.updateById(patrolTask);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "patrol_task-通过id删除")
    @ApiOperation(value = "patrol_task-通过id删除", notes = "patrol_task-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        patrolTaskService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "patrol_task-批量删除")
    @ApiOperation(value = "patrol_task-批量删除", notes = "patrol_task-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.patrolTaskService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "patrol_task-通过id查询")
    @ApiOperation(value = "patrol_task-通过id查询", notes = "patrol_task-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<PatrolTask> queryById(@RequestParam(name = "id", required = true) String id) {
        PatrolTask patrolTask = patrolTaskService.getById(id);
        if (patrolTask == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(patrolTask);
    }

    /**
     * app-巡检任务列表
     *
     * @param patrolTaskDTO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return author hlq
     */
    @AutoLog(value = "patrol_task-app分页列表查询")
    @ApiOperation(value = "patrol_task-app分页列表查询", notes = "patrol_task-app分页列表查询")
    @GetMapping(value = "/patrolTaskList")
    public Result<IPage<PatrolTaskDTO>> patrolTaskList(PatrolTaskDTO patrolTaskDTO,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        Page<PatrolTaskDTO> pageList = new Page<PatrolTaskDTO>(pageNo, pageSize);
        pageList = patrolTaskService.getPatrolTaskList(pageList, patrolTaskDTO);
        return Result.OK(pageList);
    }

    /**
     * app巡检任务领取、确认、执行、退回、执行中任务提交
     *
     * @param patrolTaskDTO
     * @param req
     * @return
     */
    @AutoLog(value = "patrol_task-app巡检任务领取")
    @ApiOperation(value = "patrol_task-app巡检任务领取", notes = "patrol_task-app巡检任务领取")
    @PostMapping(value = "/patrolTaskReceive")
    public Result<IPage<PatrolTaskDTO>> patrolTaskReceive(PatrolTaskDTO patrolTaskDTO, HttpServletRequest req) {
        patrolTaskService.getPatrolTaskReceive(patrolTaskDTO);
        return Result.OK("领取成功");
    }

    /**
     * app巡检任务领取后-退回
     *
     * @param patrolTaskDTO
     * @param req
     * @return
     */
    @AutoLog(value = "patrol_task-app巡检任务领取后-退回")
    @ApiOperation(value = "patrol_task-app巡检任务领取后-退回", notes = "patrol_task-app巡检任务领取后-退回")
    @PostMapping(value = "/patrolTaskReturn")
    public Result<IPage<PatrolTaskDTO>> patrolTaskReturn(PatrolTaskDTO patrolTaskDTO, HttpServletRequest req) {
        patrolTaskService.getPatrolTaskReturn(patrolTaskDTO);
        return Result.OK("退回成功");
    }

    /**
     * app巡检任务执行中-检查
     *
     * @param patrolTaskDTO
     * @param req
     * @return
     */
    @AutoLog(value = "patrol_task-app巡检任务执行中-检查")
    @ApiOperation(value = "patrol_task-app巡检任务执行中-检查", notes = "patrol_task-app巡检任务执行中-检查")
    @PostMapping(value = "/patrolTaskCheck")
    public Result<IPage<PatrolTaskDTO>> patrolTaskCheck(PatrolTaskDTO patrolTaskDTO, HttpServletRequest req) {
        patrolTaskService.getPatrolTaskCheck(patrolTaskDTO);
        return Result.OK("检查成功");
    }

    /**
     * app巡检任务-指派人员查询
     *
     * @param patrolTaskDTO
     * @param req
     * @return
     */
    @AutoLog(value = "patrol_task-指派人员查询")
    @ApiOperation(value = "patrol_task-指派人员查询", notes = "patrol_task-指派人员查询")
    @PostMapping(value = "/patrolTaskAppointSelect")
    public List<PatrolTaskUserDTO> patrolTaskAppointSelect(PatrolTaskDTO patrolTaskDTO, HttpServletRequest req) {
        List<PatrolTaskUserDTO> patrolTaskUserDTOS = patrolTaskService.getPatrolTaskAppointSelect(patrolTaskDTO);
        return patrolTaskUserDTOS;
    }

    /**
     * app巡检任务-指派人员
     *
     * @param patrolTaskUserDTO
     * @param req
     * @return
     */
    @AutoLog(value = "patrol_task-指派人员")
    @ApiOperation(value = "patrol_task-指派人员", notes = "patrol_task-指派人员")
    @PostMapping(value = "/patrolTaskAppoint")
    public Result<?> patrolTaskAppoint(@RequestBody List<PatrolTaskUserDTO> patrolTaskUserDTO, HttpServletRequest req) {
        patrolTaskService.getPatrolTaskAppoint(patrolTaskUserDTO);
        return Result.OK("指派成功");
    }

    /**
     * app巡检任务-驳回
     *
     * @return
     */
    @AutoLog(value = "patrol_task- app巡检任务-驳回")
    @ApiOperation(value = "patrol_task- app巡检任务-驳回", notes = "patrol_task- app巡检任务-驳回")
    @PostMapping(value = "/patrolTaskReject")
    public Result<?> patrolTaskReject(String id, String back_reason) {
        LambdaUpdateWrapper<PatrolTask> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(PatrolTask::getStatus, 5).set(PatrolTask::getRemark, back_reason).eq(PatrolTask::getId, id);
        patrolTaskService.update(queryWrapper);
        return Result.OK("驳回成功");
    }

    /**
     * app巡检任务-通过
     *
     * @return
     */
    @AutoLog(value = "patrol_task- app巡检任务-通过")
    @ApiOperation(value = "patrol_task- app巡检任务-通过", notes = "patrol_task- app巡检任务-通过")
    @PostMapping(value = "/patrolTaskPass")
    public Result<?> patrolTaskPass(String id, String back_reason) {
        LambdaUpdateWrapper<PatrolTask> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(PatrolTask::getStatus, 7).set(PatrolTask::getRemark, back_reason).eq(PatrolTask::getId, id);
        patrolTaskService.update(queryWrapper);
        return Result.OK("通过成功");
    }

    /**
     * 导出excel
     *
     * @param request
     * @param patrolTask
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolTask patrolTask) {
        return super.exportXls(request, patrolTask, PatrolTask.class, "patrol_task");
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
        return super.importExcel(request, response, PatrolTask.class);
    }

}
