package com.aiurt.modules.train.quzrtz;

import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
/**
 * @author lkj
 */
@Service
public class MakeUpExamServiceImpl {
    @Autowired
    private ISysBaseAPI sysBaseAPI;


    public void test(QuartzJobDTO quartzJobStart, QuartzJobDTO quartzJobEnd) {
        //定时开始考试
        quartzJobStart.setCreateBy("lkj");
        quartzJobStart.setCreateTime(new Date());
        quartzJobStart.setDelFlag(0);
        quartzJobStart.setUpdateTime(new Date());
        quartzJobStart.setJobClassName("com.aiurt.modules.train.quzrtz.job.MakeUpExamStartJob");
        quartzJobStart.setDescription("定时开始考试");
        quartzJobStart.setStatus(0);
        sysBaseAPI.saveAndScheduleJob(quartzJobStart);

        //定时任务结束考试
        quartzJobEnd.setCreateBy("lkj");
        quartzJobEnd.setCreateTime(new Date());
        quartzJobEnd.setDelFlag(0);
        quartzJobEnd.setUpdateTime(new Date());
        quartzJobEnd.setJobClassName("com.aiurt.modules.train.quzrtz.job.MakeUpExamEndJob");
        quartzJobEnd.setDescription("定时任务结束考试");
        quartzJobEnd.setStatus(0);
        sysBaseAPI.saveAndScheduleJob(quartzJobEnd);
    }
}
