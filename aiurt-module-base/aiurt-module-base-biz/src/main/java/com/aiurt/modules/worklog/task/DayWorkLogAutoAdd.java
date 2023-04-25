package com.aiurt.modules.worklog.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.mapper.WorkLogMapper;
import com.aiurt.modules.worklog.service.impl.WorkLogServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.SysDepartModel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author lkj
 */
@Slf4j
@Component
public class DayWorkLogAutoAdd implements Job {
    @Autowired
    private WorkLogServiceImpl workLogService;
    @Autowired
    private WorkLogMapper workLogMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(JobExecutionContext context) throws JobExecutionException {

        log.info("******正在生成白班工作日志...******");
        autoAdd();
        log.info("******白班工作日志生成完成！*******");
    }


    @Transactional(rollbackFor = Exception.class)
    public void execute() {
        log.info("******正在生成白班工作日志...******");
        autoAdd();
        log.info("******白班工作日志生成完成！*******");
    }

    /**
     * 每天下午16点生成白班工作日志
     */
    private void autoAdd() {
        List<SysDepartModel> allSysDepart = iSysBaseAPI.getAllSysDepart();
        if (CollUtil.isNotEmpty(allSysDepart)) {
            for (SysDepartModel sysDepartModel : allSysDepart) {
                WorkLog depot = new WorkLog();
                depot.setOrgId(sysDepartModel.getId());
                depot.setCreateTime(DateUtil.parse(DateUtil.today()+" 16:00:00", "yyyy-MM-dd HH:mm:ss"));
                String logCode = workLogService.generateLogCode();
                depot.setCode(logCode);

                depot.setStatus(1);
                depot.setConfirmStatus(0);
                depot.setCheckStatus(0);
                if (depot.getStatus()==1){
                    depot.setSubmitTime(new Date());
                }
                //工作内容赋值
                depot.setIsEmergencyDisposal(0);
                depot.setIsDocumentPublicity(0);

                depot.setNote("需穿戴工装，工作证上岗，维修及巡检时全程带好口罩");

                depot.setLogTime(DateUtil.parse(DateUtil.today(), "yyyy-MM-dd"));
                depot.setDelFlag(0);
                workLogMapper.insert(depot);
            }
        }
    }
}
