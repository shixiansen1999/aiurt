package com.aiurt.boot.task.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.aiurt.boot.task.service.IPatrolAccompanyService;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import com.aiurt.boot.task.service.IPatrolTaskOrganizationService;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @Autowired
    private IPatrolAccompanyService patrolAccompanyService;

    @Autowired
    private IPatrolTaskOrganizationService patrolTaskOrganizationService;


    /**
     * PC巡检任务列表-分页列表查询
     *
     * @param patrolTaskParam
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "PC巡检任务列表-分页列表查询")
    @ApiOperation(value = "PC巡检任务列表-分页列表查询", notes = "PC巡检任务列表-分页列表查询")
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<IPage<PatrolTaskParam>> queryPageList(PatrolTaskParam patrolTaskParam,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        HttpServletRequest req) {
        Page<PatrolTaskParam> page = new Page<PatrolTaskParam>(pageNo, pageSize);
        IPage<PatrolTaskParam> pageList = patrolTaskService.getTaskList(page, patrolTaskParam);
        return Result.OK(pageList);
    }

    /**
     * PC巡检任务池详情-基本信息
     *
     * @param patrolTaskParam
     * @param req
     * @return
     */
    @AutoLog(value = "PC巡检任务池详情-基本信息")
    @ApiOperation(value = "PC巡检任务池详情-基本信息", notes = "PC巡检任务池详情-基本信息")
    @RequestMapping(value = "/basicInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<PatrolTaskParam> selectBasicInfo(@RequestBody PatrolTaskParam patrolTaskParam,
                                                   HttpServletRequest req) {
        PatrolTaskParam task = patrolTaskService.selectBasicInfo(patrolTaskParam);
        return Result.OK(task);
    }

    /**
     * PC巡检任务池详情-巡检工单
     *
     * @param patrolTaskDeviceParam
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "PC巡检任务池详情-巡检工单")
    @ApiOperation(value = "PC巡检任务池详情-巡检工单", notes = "PC巡检任务池详情-巡检工单")
    @RequestMapping(value = "/billInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> selectBillInfo(PatrolTaskDeviceParam patrolTaskDeviceParam,
                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                    HttpServletRequest req) {
        Page<PatrolTaskDeviceParam> page = new Page<>(pageNo, pageSize);
        IPage<PatrolTaskDeviceParam> taskDevicePageList = patrolTaskDeviceService.selectBillInfo(page, patrolTaskDeviceParam);
        return Result.OK(taskDevicePageList);
    }

    @AutoLog(value = "PC设备台账-巡视履历")
    @ApiOperation(value = "PC设备台账-巡视履历", notes = "PC设备台账-巡视履历")
    @RequestMapping(value = "/billInfoForDevice", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> selectBillInfoForDevice(PatrolTaskDeviceParam patrolTaskDeviceForDeviceParam,
                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             HttpServletRequest req) {
        Page<PatrolTaskDeviceParam> page = new Page<>(pageNo, pageSize);
        IPage<PatrolTaskDeviceParam> taskDevicePageList = patrolTaskDeviceService.selectBillInfoForDevice(page, patrolTaskDeviceForDeviceParam);
        return Result.OK(taskDevicePageList);
    }

    @AutoLog(value = "PC巡检任务池详情-根据任务id获得专业和子系统信息")
    @ApiOperation(value = "PC巡检任务池详情-根据任务id获得专业和子系统信息", notes = "PC巡检任务池详情-根据任务id获得专业和子系统信息")
    @RequestMapping(value = "/getMajorSystemInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> getMajorSubsystemGanged(@RequestParam("id") String id) {
        Map<String, Object> map = patrolTaskService.getMajorSubsystemGanged(id);
        return Result.OK(map);
    }

    /**
     * PC巡检任务池详情-巡检工单详情
     *
     * @param patrolNumber
     * @param req
     * @return
     */
    @AutoLog(value = "PC巡检任务池详情-巡检工单详情")
    @ApiOperation(value = "PC巡检任务池详情-巡检工单详情", notes = "PC巡检任务池详情-巡检工单详情")
    @RequestMapping(value = "/billInfoByNumber", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> selectBillInfoByNumber(@RequestParam("patrolNumber") String patrolNumber, HttpServletRequest req) {
        Map<String, Object> map = patrolTaskDeviceService.selectBillInfoByNumber(patrolNumber);
        return Result.OK(map);
    }

    /**
     * PC巡检任务池-获取指派人员
     *
     * @param list
     * @return
     */
    @AutoLog(value = "PC巡检任务池-获取指派人员")
    @ApiOperation(value = "PC巡检任务池-获取指派人员", notes = "PC巡检任务池-获取指派人员")
    @RequestMapping(value = "/getAssignee", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<?> getAssignee(@RequestParam("code") List<String> list) {
        List<PatrolUserInfoDTO> userInfo = patrolTaskService.getAssignee(list);
        return Result.OK(userInfo);
    }

    /**
     * PC巡检任务池-任务指派
     *
     * @return
     */
    @AutoLog(value = "PC巡检任务池-任务指派")
    @ApiOperation(value = "PC巡检任务池-任务指派", notes = "PC巡检任务池-任务指派")
    @PostMapping(value = "/taskAppoint")
    public Result<?> taskAppoint(@RequestBody PatrolAppointInfoDTO patrolAppointInfoDTO) {
        int reslut = patrolTaskService.taskAppoint(patrolAppointInfoDTO);
        return Result.OK("成功对" + reslut + "条任务进行指派！", null);
    }

    /**
     * PC巡检任务池-任务作废
     *
     * @param list
     * @return
     */
    @AutoLog(value = "PC巡检任务池-任务作废")
    @ApiOperation(value = "PC巡检任务池-任务作废", notes = "PC巡检任务池-任务作废")
    @PostMapping(value = "/taskDiscard")
    public Result<?> taskDiscard(@RequestBody List<PatrolTask> list) {
        int reslut = patrolTaskService.taskDiscard(list);
        return Result.OK("成功对" + reslut + "条任务进行作废操作！", null);
    }

    /**
     * PC巡检任务列表-任务审核
     *
     * @param code
     * @param auditStatus 审核状态，通过/不通过
     * @param auditReason
     * @param remark
     * @return
     */
    @AutoLog(value = "PC巡检任务列表-任务审核")
    @ApiOperation(value = "PC巡检任务列表-任务审核", notes = "PC巡检任务列表-任务审核")
    @PostMapping(value = "/taskAudit")
    public Result<?> taskAudit(@RequestParam("taskCode") String code, @RequestParam("auditStatus") Integer auditStatus,
                               String auditReason, String remark) {
        patrolTaskService.taskAudit(code, auditStatus, auditReason, remark);
        return Result.OK();
    }

    /**
     * 巡检漏检任务处理-处置返显信息
     *
     * @param id
     * @return
     */
    @AutoLog(value = "巡检漏检任务处理-处置返显信息")
    @ApiOperation(value = "巡检漏检任务处理-处置返显信息", notes = "巡检漏检任务处理-处置返显信息")
    @RequestMapping(value = "/getDisposeInfo", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<?> taskDisposeInfo(@ApiParam(name = "id", value = "任务记录ID") @RequestParam("id") String id) {
        PatrolTask task = patrolTaskService.getById(id);
        PatrolDisposeDTO dispose = new PatrolDisposeDTO();
        dispose.setTaskCode(task.getCode());
        dispose.setTaskName(task.getName());
        dispose.setOmitDate(task.getPatrolDate());
        return Result.OK(dispose);
    }

    /**
     * 巡检漏检任务处理-处置
     *
     * @param id
     * @return
     */
    @AutoLog(value = "巡检漏检任务处理-处置")
    @ApiOperation(value = "巡检漏检任务处理-处置", notes = "巡检漏检任务处理-处置")
    @PostMapping(value = "/dispose")
    public Result<?> taskDispose(@ApiParam(name = "id", value = "任务记录ID") @RequestParam("id") String id,
                                 @ApiParam(name = "omitExplain", value = "漏检说明") @RequestParam("omitExplain") String omitExplain) {
        PatrolTask task = patrolTaskService.getById(id);
        if (ObjectUtil.isEmpty(task)) {
            return Result.error("记录不存在！");
        }
        int record = patrolTaskService.taskDispose(task, omitExplain);
        return Result.OK("成功处置" + record + "条漏检任务记录！", null);
    }
    /**
     * 添加
     *
     * @param patrolTask
     * @return
     */
   /* @AutoLog(value = "patrol_task-添加")
    @ApiOperation(value = "patrol_task-添加", notes = "patrol_task-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody PatrolTask patrolTask) {
        patrolTaskService.save(patrolTask);
        return Result.OK("添加成功！");
    }
*/
    /**
     * 编辑
     *
     * @param patrolTask
     * @return
     */
   /* @AutoLog(value = "patrol_task-编辑")
    @ApiOperation(value = "patrol_task-编辑", notes = "patrol_task-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody PatrolTask patrolTask) {
        patrolTaskService.updateById(patrolTask);
        return Result.OK("编辑成功!");
    }*/

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    /*@AutoLog(value = "patrol_task-通过id删除")
    @ApiOperation(value = "patrol_task-通过id删除", notes = "patrol_task-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        patrolTaskService.removeById(id);
        return Result.OK("删除成功!");
    }
*/
    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    /*@AutoLog(value = "patrol_task-批量删除")
    @ApiOperation(value = "patrol_task-批量删除", notes = "patrol_task-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.patrolTaskService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }*/

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    /*//@AutoLog(value = "patrol_task-通过id查询")
    @ApiOperation(value = "patrol_task-通过id查询", notes = "patrol_task-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<PatrolTask> queryById(@RequestParam(name = "id", required = true) String id) {
        PatrolTask patrolTask = patrolTaskService.getById(id);
        if (patrolTask == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(patrolTask);
    }*/

    /**
     * app-巡检任务池
     *
     * @param patrolTaskDTO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return author hlq
     */
    @AutoLog(value = "巡检任务表-app巡检任务池")
    @ApiOperation(value = "巡检任务表-app巡检任务池", notes = "巡检任务表-app巡检任务池")
    @GetMapping(value = "/patrolTaskPoolList")
    public Result<IPage<PatrolTaskDTO>> patrolTaskPoolList(PatrolTaskDTO patrolTaskDTO,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           HttpServletRequest req) {
        Page<PatrolTaskDTO> pageList = new Page<PatrolTaskDTO>(pageNo, pageSize);
        pageList = patrolTaskService.getPatrolTaskPoolList(pageList, patrolTaskDTO);
        return Result.OK(pageList);
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
    @AutoLog(value = "巡检任务表-app巡检任务列表")
    @ApiOperation(value = "巡检任务表-app巡检任务列表", notes = "巡检任务表-app巡检任务列表")
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
     * app巡检任务领取、确认、执行
     *
     * @param patrolTaskDTO
     * @param req
     * @return
     */
    @AutoLog(value = "巡检任务表-app巡检任务领取")
    @ApiOperation(value = "巡检任务表-app巡检任务领取", notes = "巡检任务表-app巡检任务领取")
    @PostMapping(value = "/patrolTaskReceive")
    public Result<?> patrolTaskReceive(PatrolTaskDTO patrolTaskDTO, HttpServletRequest req) {
        patrolTaskService.getPatrolTaskReceive(patrolTaskDTO);
        if (patrolTaskDTO.getStatus() == 1) {
            return Result.OK("确认成功");
        }
        if (patrolTaskDTO.getStatus() == 2) {
            return Result.OK("执行成功");
        }
        return Result.OK("领取成功");
    }

    /**
     * app巡检任务提交
     *
     * @param patrolTaskDTO
     * @param req
     * @return
     */
    @AutoLog(value = "巡检任务表-app巡检任务提交")
    @ApiOperation(value = "巡检任务表-app巡检任务提交", notes = "巡检任务表-app巡检任务提交")
    @PostMapping(value = "/patrolTaskSubmit")
    public Result<?> patrolTaskSubmit(@RequestBody PatrolTaskDTO patrolTaskDTO, HttpServletRequest req) {
        patrolTaskService.getPatrolTaskSubmit(patrolTaskDTO);
        return Result.OK("提交任务成功");
    }

    /**
     * app巡检任务提交-统计工单数量
     *
     * @param patrolTaskSubmitDTO
     * @param req
     * @return
     */
    @AutoLog(value = "巡检任务表-app巡检任务提交-统计工单数量")
    @ApiOperation(value = "巡检任务表-app巡检任务提交-统计工单数量", notes = "巡检任务表-app巡检任务提交-统计工单数量")
    @PostMapping(value = "/submitTaskCount")
    public Result<?> submitTaskCount(PatrolTaskSubmitDTO patrolTaskSubmitDTO, HttpServletRequest req) {
        PatrolTaskSubmitDTO submitTaskCount = patrolTaskService.getSubmitTaskCount(patrolTaskSubmitDTO);
        return Result.OK("领取成功");
    }

    /**
     * app巡检任务-退回
     *
     * @param patrolTaskDTO
     * @param req
     * @return
     */
    @AutoLog(value = "巡检任务表-app巡检任务-退回")
    @ApiOperation(value = "巡检任务表-app巡检任务-退回", notes = "巡检任务表-app巡检任务-退回")
    @PostMapping(value = "/patrolTaskReturn")
    public Result<?> patrolTaskReturn(PatrolTaskDTO patrolTaskDTO, HttpServletRequest req) {
        patrolTaskService.getPatrolTaskReturn(patrolTaskDTO);
        return Result.OK("退回成功");
    }

    /**
     * app巡检任务-指派人员查询
     *
     * @param patrolTaskDTO
     * @param req
     * @return
     */
    @AutoLog(value = "同行人-同行人查询")
    @ApiOperation(value = "巡检任务表-指派人员查询", notes = "巡检任务表-指派人员查询")
    @PostMapping(value = "/patrolTaskAppointSelect")
    public Result<?> patrolTaskAppointSelect(@RequestBody PatrolOrgDTO orgCoed, HttpServletRequest req) {
        List<PatrolTaskUserDTO> patrolTaskUserDTOS = patrolTaskService.getPatrolTaskAppointSelect(orgCoed);
        return Result.OK(patrolTaskUserDTOS);
    }

    /**
     * app巡检任务-指派人员
     *
     * @param patrolTaskUserDTO
     * @param req
     * @return
     */
    @AutoLog(value = "巡检任务表-指派人员")
    @ApiOperation(value = "巡检任务表-指派人员", notes = "巡检任务表-指派人员")
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
    @AutoLog(value = "巡检任务表- app巡检任务-驳回")
    @ApiOperation(value = "巡检任务表- app巡检任务-驳回", notes = "巡检任务表- app巡检任务-驳回")
    @PostMapping(value = "/patrolTaskReject")
    public Result<?> patrolTaskReject(String id, String backReason) {
        LambdaUpdateWrapper<PatrolTask> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(PatrolTask::getStatus, 5).set(PatrolTask::getRemark, backReason).eq(PatrolTask::getId, id);
        patrolTaskService.update(queryWrapper);
        return Result.OK("驳回成功");
    }

    /**
     * app巡检任务-通过
     *
     * @return
     */
    @AutoLog(value = "巡检任务表- app巡检任务-通过")
    @ApiOperation(value = "巡检任务表- app巡检任务-通过", notes = "巡检任务表- app巡检任务-通过")
    @PostMapping(value = "/patrolTaskPass")
    public Result<?> patrolTaskPass(String id, String backReason) {
        LambdaUpdateWrapper<PatrolTask> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(PatrolTask::getStatus, 7).set(PatrolTask::getRemark, backReason).eq(PatrolTask::getId, id);
        patrolTaskService.update(queryWrapper);
        return Result.OK("通过成功");
    }

    /**
     * pc手工下放任务列表
     *
     * @return
     */
    @AutoLog(value = "PC手工下放任务列表")
    @ApiOperation(value = "PC手工下放任务列表", notes = "PC手工下放任务列表")
    @PostMapping(value = "/patrolTaskManual")
    public Result<?> patrolTaskManual(PatrolTaskDTO patrolTaskDTO,
                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Page<PatrolTaskDTO> pageList = new Page<PatrolTaskDTO>(pageNo, pageSize);
        pageList = patrolTaskService.getPatrolTaskManualList(pageList, patrolTaskDTO);
        return Result.OK(pageList);
    }

    /**
     * pc手工下放任务-新增
     *
     * @return
     */
    @AutoLog(value = "pc手工下放任务-新增")
    @ApiOperation(value = "PC手工下放任务列表-新增", notes = "PC手工下放任务列表-新增")
    @PostMapping(value = "/patrolTaskManualAdd")
    public Result<?> patrolTaskManualAdd(@RequestBody PatrolTaskManualDTO patrolTaskManualDTO,
                                         HttpServletRequest req) {
        patrolTaskService.getPatrolTaskManualListAdd(patrolTaskManualDTO);
        return Result.OK("新增成功");
    }

    /**
     * pc手工下放任务-编辑-详情
     *
     * @return
     */
    @AutoLog(value = "pc手工下放任务-编辑-详情")
    @ApiOperation(value = "pc手工下放任务-编辑-详情", notes = "pc手工下放任务-编辑-详情")
    @PostMapping(value = "/patrolTaskManualDetail")
    public Result<?> patrolTaskManualDetail(String id,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Page<PatrolTaskStandardDTO> pageList = new Page<PatrolTaskStandardDTO>(pageNo, pageSize);
        pageList = patrolTaskService.getPatrolTaskManualDetail(pageList, id);
        return Result.OK(pageList);
    }
    /**
     * 导出excel
     *
     * @param request
     * @param patrolTask
     */
   /* @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolTask patrolTask) {
        return super.exportXls(request, patrolTask, PatrolTask.class, "patrol_task");
    }
*/
    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    /*@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PatrolTask.class);
    }*/

}
