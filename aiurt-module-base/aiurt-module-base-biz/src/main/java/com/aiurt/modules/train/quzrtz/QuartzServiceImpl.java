package com.aiurt.modules.train.quzrtz;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import org.jeecg.common.system.api.ISysBaseAPI;
import com.aiurt.modules.train.quzrtz.job.CronUtlit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class QuartzServiceImpl   {

    @Autowired
    private ISysBaseAPI sysBaseAPI;


    public void test(QuartzJobDTO quartzJobDTO) {

        quartzJobDTO.setCreateBy("zwl");
        quartzJobDTO.setCreateTime(new Date());
        quartzJobDTO.setDelFlag(0);
        quartzJobDTO.setUpdateTime(new Date());
        quartzJobDTO.setJobClassName("org.jeecg.modules.train.quzrtz.job.SampleJob");
        //参数
        quartzJobDTO.setDescription("");
        quartzJobDTO.setStatus(0);

        sysBaseAPI.saveAndScheduleJob(quartzJobDTO);

    }
}
