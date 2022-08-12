package com.aiurt.modules.worklog.service.impl;

import com.aiurt.common.constant.QuartConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.QuartzUtils;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.worklog.dto.WorkLogJobDTO;
import com.aiurt.modules.worklog.dto.WorkLogRemindDTO;
import com.aiurt.modules.worklog.entity.WorkLogRemind;
import com.aiurt.modules.worklog.mapper.WorkLogRemindMapper;
import com.aiurt.modules.worklog.service.IWorkLogRemindService;
import com.aiurt.modules.worklog.task.WorkLogJob;
import com.aiurt.modules.worklog.task.WorkLogJobNight;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Service
public class WorkLogRemindServiceImpl extends ServiceImpl<WorkLogRemindMapper, WorkLogRemind> implements IWorkLogRemindService {

    @Autowired
    private QuartzUtils quartzUtils;
    @Autowired
    private WorkLogRemindMapper workLogRemindMapper;


    /**
     * 添加提醒时间设置
     *
     * @param dto
     * @param req
     * @return
     */
    @Override
    public Result add(WorkLogRemindDTO dto, HttpServletRequest req) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据当前登录 人的部门，查同部门的最后的一个时间提醒
        WorkLogRemind one = this.lambdaQuery().eq(WorkLogRemind::getOrgId, user.getOrgId()).last("limit 1").one();
        WorkLogRemind remind = new WorkLogRemind();
        if (one!=null){
            remind.setId(one.getId());
        }
        if (dto==null || dto.getRemindTime()==null|| dto.getRemindTimeNight()==null){
            return Result.error("提醒时间不能为空");
        }
        String cron = dto.getRemindTime().getSecond() + " " + dto.getRemindTime().getMinute() + " " + dto.getRemindTime().getHour() + " " + "* * ?";
        String cronNight = dto.getRemindTimeNight().getSecond() + " " + dto.getRemindTimeNight().getMinute() + " " + dto.getRemindTimeNight().getHour() + " " + "* * ?";
        remind.setOrgId(user.getOrgId())
                .setOrgName(user.getOrgName())
                .setRemindTime(dto.getRemindTime())
                .setRemindTimeNight(dto.getRemindTimeNight())
                .setContent(dto.getContent())
                .setCron(cron)
                .setCronNight(cronNight)
                .setDelFlag(0);
        this.saveOrUpdate(remind);
        //添加调度任务
        WorkLogJobDTO workLogJobDTO = new WorkLogJobDTO();
        workLogJobDTO.setOrgId(remind.getOrgId());
        workLogJobDTO.setContent(remind.getContent());
        workLogJobDTO.setFromUser(remind.getCreateBy());
        Map<String, Object> map = new HashMap<>();
        map.put("orgId", workLogJobDTO);


        try {
            quartzUtils.modifyJob(QuartConstant.WORK_LOG_JOB + remind.getOrgId(),
                    QuartConstant.WORK_LOG_JOB + remind.getOrgId() + QuartConstant.GROUP,
                    QuartConstant.WORK_LOG_TRIGGER + remind.getOrgId(),
                    QuartConstant.WORK_LOG_TRIGGER + remind.getOrgId() + QuartConstant.GROUP, WorkLogJob.class, cron, map);
        } catch (Exception e) {
            try {
                quartzUtils.addJob(QuartConstant.WORK_LOG_JOB + remind.getOrgId(),
                        QuartConstant.WORK_LOG_JOB + remind.getOrgId() + QuartConstant.GROUP,
                        QuartConstant.WORK_LOG_TRIGGER + remind.getOrgId(),
                        QuartConstant.WORK_LOG_TRIGGER + remind.getOrgId() + QuartConstant.GROUP, WorkLogJob.class, cron, map);
            } catch (Exception ex) {
                throw new AiurtBootException("设置白班提醒时间出现错误,请稍后重试");
            }
        }
        //夜班调度任务
        try {
            quartzUtils.modifyJob(QuartConstant.WORK_LOG_JOB_NIGHT + remind.getOrgId(),
                    QuartConstant.WORK_LOG_JOB_NIGHT + remind.getOrgId() + QuartConstant.GROUP,
                    QuartConstant.WORK_LOG_TRIGGER_NIGHT + remind.getOrgId(),
                    QuartConstant.WORK_LOG_TRIGGER_NIGHT + remind.getOrgId() + QuartConstant.GROUP, WorkLogJobNight.class, cronNight, map);
        } catch (Exception e) {
            try {
                quartzUtils.addJob(QuartConstant.WORK_LOG_JOB_NIGHT + remind.getOrgId(),
                        QuartConstant.WORK_LOG_JOB_NIGHT + remind.getOrgId() + QuartConstant.GROUP,
                        QuartConstant.WORK_LOG_TRIGGER_NIGHT + remind.getOrgId(),
                        QuartConstant.WORK_LOG_TRIGGER_NIGHT + remind.getOrgId() + QuartConstant.GROUP, WorkLogJobNight.class, cronNight, map);
            } catch (Exception ex) {
                throw new AiurtBootException("设置夜班提醒时间出现错误,请稍后重试");
            }
        }
        return Result.ok("添加成功");
    }

    /**
     * 获取当登陆人所在班组日志提醒信息
     *
     * @param req
     * @return
     */
    @Override
    public WorkLogRemind getWorkLogRemind(HttpServletRequest req) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        WorkLogRemind workLogRemind = this.getOne(new QueryWrapper<WorkLogRemind>().eq(WorkLogRemind.ORG_ID, user.getOrgId()));
        return workLogRemind;
    }

    @Override
    public List<ScheduleRecord> getOrgUserTodayWork(String dateNow, String orgId) {
        List<ScheduleRecord> userName = workLogRemindMapper.getOrgUserTodayWork(dateNow,orgId);
        return userName;
    }


    @PostConstruct
    public void initJob(){
        List<WorkLogRemind> list = this.lambdaQuery().eq(WorkLogRemind::getDelFlag, CommonConstant.DEL_FLAG_0).list();
        if (CollectionUtils.isNotEmpty(list)){
            for (WorkLogRemind remind : list) {
                try {
                    WorkLogJobDTO dto = new WorkLogJobDTO();
                    dto.setContent(remind.getContent());
                    dto.setOrgId(remind.getOrgId());
                    Map<String, Object> map = new HashMap<>();
                    map.put("orgId", dto);
                    quartzUtils.addJob(QuartConstant.WORK_LOG_JOB + remind.getOrgId(),
                            QuartConstant.WORK_LOG_JOB + remind.getOrgId() + QuartConstant.GROUP,
                            QuartConstant.WORK_LOG_TRIGGER + remind.getOrgId(),
                            QuartConstant.WORK_LOG_TRIGGER + remind.getOrgId() + QuartConstant.GROUP, WorkLogJob.class, remind.getCron(), map);
                    //夜班调度任务
                    quartzUtils.addJob(QuartConstant.WORK_LOG_JOB_NIGHT + remind.getOrgId(),
                            QuartConstant.WORK_LOG_JOB_NIGHT + remind.getOrgId() + QuartConstant.GROUP,
                            QuartConstant.WORK_LOG_TRIGGER_NIGHT + remind.getOrgId(),
                            QuartConstant.WORK_LOG_TRIGGER_NIGHT + remind.getOrgId() + QuartConstant.GROUP, WorkLogJobNight.class, remind.getCronNight(), map);
                } catch (Exception ignored) {
                }
            }
        }
    }
}

