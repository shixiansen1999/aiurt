package com.aiurt.modules.robot.controller;

import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.modules.robot.dto.TaskFinishDTO;
import com.aiurt.modules.robot.taskfinish.service.TaskFinishService;
import com.aiurt.modules.robot.vo.TaskFinishInfoVO;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.robot.entity.TaskFinishInfo;
import com.aiurt.modules.robot.service.ITaskFinishInfoService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

/**
 * @Description: task_finish_info
 * @Author: aiurt
 * @Date: 2022-09-28
 * @Version: V1.0
 */
@Api(tags = "机器人巡检任务")
@RestController
@RequestMapping("/robot/taskFinishInfo")
@Slf4j
public class TaskFinishInfoController extends BaseController<TaskFinishInfo, ITaskFinishInfoService> {
    @Autowired
    private ITaskFinishInfoService taskFinishInfoService;
    @Autowired
    private TaskFinishService taskFinishService;

    /**
     * 机器人巡检任务列表查询
     *
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "机器人巡检任务列表查询")
    @ApiOperation(value = "机器人巡检任务列表查询", notes = "机器人巡检任务列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TaskFinishInfoVO>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         TaskFinishDTO taskFinishDTO, HttpServletRequest req) {
        Page<TaskFinishInfoVO> page = new Page<TaskFinishInfoVO>(pageNo, pageSize);
        IPage<TaskFinishInfoVO> pageList = taskFinishInfoService.queryPageList(page, taskFinishDTO);
        return Result.OK(pageList);
    }

//    /**
//     * 刷新同步巡检任务数据
//     *
//     * @param taskFinishInfo
//     * @return
//     */
//    @AutoLog(value = "刷新同步巡检任务数据")
//    @ApiOperation(value = "刷新同步巡检任务数据", notes = "刷新同步巡检任务数据")
//    @PostMapping(value = "/add")
//    public Result<String> add(@RequestBody TaskFinishInfo taskFinishInfo) {
//        taskFinishInfoService.save(taskFinishInfo);
//        return Result.OK("添加成功！");
//    }

    /**
     * 刷新同步巡检任务数据
     *
     * @return
     */
    @AutoLog(value = "刷新同步巡检任务数据")
    @ApiOperation(value = "刷新同步巡检任务数据", notes = "刷新同步巡检任务数据")
    @PostMapping(value = "/synchronizeRobotTask")
    public Result<String> synchronizeRobotTask(@ApiParam(name = "startDate", value = "起始时间")
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
                                               @ApiParam(name = "endTime", value = "结束时间")
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        taskFinishInfoService.synchronizeRobotTask(startTime, endTime);
        return Result.OK("同步巡检任务数据完成！");
    }

    /**
     * 机器人巡检任务处置
     *
     * @param id
     * @param handleExplain
     * @return
     */
    @AutoLog(value = "机器人巡检任务处置")
    @ApiOperation(value = "机器人巡检任务处置", notes = "机器人巡检任务处置")
    @RequestMapping(value = "/taskDispose", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> taskDispose(@ApiParam(name = "id", value = "任务记录ID")
                                      @RequestParam("id") String id,
                                      @ApiParam(name = "handleExplain", value = "处置说明")
                                      @RequestParam("handleExplain") String handleExplain) {
        taskFinishInfoService.taskDispose(id, handleExplain);
        return Result.OK("任务处置成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "task_finish_info-通过id删除")
    @ApiOperation(value = "task_finish_info-通过id删除", notes = "task_finish_info-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        taskFinishInfoService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "task_finish_info-批量删除")
    @ApiOperation(value = "task_finish_info-批量删除", notes = "task_finish_info-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.taskFinishInfoService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "task_finish_info-通过id查询")
    @ApiOperation(value = "task_finish_info-通过id查询", notes = "task_finish_info-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TaskFinishInfo> queryById(@RequestParam(name = "id", required = true) String id) {
        TaskFinishInfo taskFinishInfo = taskFinishInfoService.getById(id);
        if (taskFinishInfo == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(taskFinishInfo);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param taskFinishInfo
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TaskFinishInfo taskFinishInfo) {
        return super.exportXls(request, taskFinishInfo, TaskFinishInfo.class, "task_finish_info");
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
        return super.importExcel(request, response, TaskFinishInfo.class);
    }

}
