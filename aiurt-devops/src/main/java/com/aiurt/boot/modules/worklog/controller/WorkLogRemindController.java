package com.aiurt.boot.modules.worklog.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.modules.worklog.dto.WorkLogJobDTO;
import com.aiurt.boot.modules.worklog.dto.WorkLogRemindDTO;
import com.aiurt.boot.modules.worklog.entity.WorkLogRemind;
import com.aiurt.boot.modules.worklog.service.IWorkLogRemindService;
import com.aiurt.boot.modules.worklog.task.WorkLogJob;
import com.aiurt.boot.modules.worklog.task.WorkLogJobNight;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.QuartConstant;
import com.aiurt.common.util.QuartzUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author WangHongTao
 * @Date 2021/11/30
 */
@Slf4j
@Api(tags="工作日志提醒时间设置")
@RestController
@RequestMapping("/worklog/workLogRemind")
public class WorkLogRemindController {

    @Resource
    private IWorkLogRemindService workLogRemindService;

    @Resource
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    private QuartzUtils quartzUtils;

    /**
     *   添加工作日志提醒时间设置
     * @param dto
     * @return
     */
    @AutoLog(value = "添加工作日志提醒时间设置")
    @ApiOperation(value="添加工作日志提醒时间设置", notes="添加工作日志提醒时间设置")
    @PostMapping(value = "/add")
    public Result<WorkLogRemind> add(@Valid @RequestBody WorkLogRemindDTO dto, HttpServletRequest req) {
        Result<WorkLogRemind> result = new Result<WorkLogRemind>();
        try {
            workLogRemindService.add(dto,req);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 获取当登陆人所在班组日志提醒信息
     * @param req
     * @return
     */
    @AutoLog(value = "获取当登陆人所在班组日志提醒信息")
    @ApiOperation(value="获取当登陆人所在班组日志提醒信息", notes="获取当登陆人所在班组日志提醒信息")
    @GetMapping(value = "/getWorkLogRemind")
    public Result<?> getWorkLogRemind(HttpServletRequest req) {
        WorkLogRemind workLogRemind = workLogRemindService.getWorkLogRemind(req);
        if (ObjectUtil.isEmpty(workLogRemind)) {
            return Result.ok("当前班组没有日志提醒信息");
        }
        return Result.ok(workLogRemind);
    }

    /**
     * 编辑
     * @param remind
     * @param req
     * @return
     */
    @AutoLog(value = "编辑")
    @ApiOperation(value = "编辑", notes = "编辑")
    @PutMapping(value = "/edit")
    public Result<WorkLogRemind> edit(@RequestBody WorkLogRemind remind,HttpServletRequest req) {
        Result<WorkLogRemind> result = new Result<>();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgId = user.getOrgId();
        String userId = user.getId();


        WorkLogRemind workLogRemind = workLogRemindService.getById(remind.getId());
        if (workLogRemind == null) {
            result.error500("未找到对应实体");
        } else {
            //删除调度任务
            quartzUtils.removeJob(QuartConstant.WORK_LOG_JOB +orgId,
                    QuartConstant.WORK_LOG_JOB+orgId+QuartConstant.GROUP,
                    QuartConstant.WORK_LOG_TRIGGER + orgId,
                    QuartConstant.WORK_LOG_TRIGGER+ orgId+QuartConstant.GROUP);
            //删除夜班提醒调度任务
            quartzUtils.removeJob(QuartConstant.WORK_LOG_JOB_NIGHT +orgId,
                    QuartConstant.WORK_LOG_JOB_NIGHT+orgId+QuartConstant.GROUP,
                    QuartConstant.WORK_LOG_TRIGGER_NIGHT + orgId,
                    QuartConstant.WORK_LOG_TRIGGER_NIGHT+ orgId+QuartConstant.GROUP);
            workLogRemind.setContent(remind.getContent()).setRemindTime(remind.getRemindTime()).setRemindTimeNight(remind.getRemindTimeNight());
            workLogRemind.setUpdateBy(userId);

            //重新添加调度任务
            WorkLogJobDTO workLogJobDTO = new WorkLogJobDTO();
            workLogJobDTO.setOrgId(orgId);
            workLogJobDTO.setContent(workLogRemind.getContent());
            Map<String, Object> map = new HashMap<>();
            map.put("orgId",workLogJobDTO);
            //白班调度任务
            LocalTime remindTime = workLogRemind.getRemindTime();
            int hour = remindTime.getHour();
            int minute = remindTime.getMinute();
            int second = remindTime.getSecond();
            String cron = second + " " + minute + " "+ hour + " " + "* * ?";
            quartzUtils.addJob(QuartConstant.WORK_LOG_JOB +workLogRemind.getOrgId(),
                    QuartConstant.WORK_LOG_JOB+workLogRemind.getOrgId()+QuartConstant.GROUP,
                    QuartConstant.WORK_LOG_TRIGGER + workLogRemind.getOrgId(),
                    QuartConstant.WORK_LOG_TRIGGER+ workLogRemind.getOrgId()+QuartConstant.GROUP, WorkLogJob.class,cron,map);

            //夜班调度任务
            LocalTime remindTimeNight = workLogRemind.getRemindTimeNight();
            int hourNight = remindTimeNight.getHour();
            int minuteNight = remindTimeNight.getMinute();
            int secondNight = remindTimeNight.getSecond();
            String cronNight = secondNight + " " + minuteNight + " "+ hourNight + " " + "* * ?";
            quartzUtils.addJob(QuartConstant.WORK_LOG_JOB_NIGHT +workLogRemind.getOrgId(),
                    QuartConstant.WORK_LOG_JOB_NIGHT+workLogRemind.getOrgId()+QuartConstant.GROUP,
                    QuartConstant.WORK_LOG_TRIGGER_NIGHT + workLogRemind.getOrgId(),
                    QuartConstant.WORK_LOG_TRIGGER_NIGHT+ workLogRemind.getOrgId()+QuartConstant.GROUP, WorkLogJobNight.class,cronNight,map);

            boolean ok = workLogRemindService.updateById(workLogRemind);
            if (ok) {
                result.success("修改成功!");
            }
        }
        return result;
    }
}
