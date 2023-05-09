package com.aiurt.modules.worklog.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.mapper.WorkLogMapper;
import com.aiurt.modules.worklog.service.impl.WorkLogServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.system.vo.SysParamModel;
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
public class NightWorkLogAutoAdd implements Job {
    @Autowired
    private WorkLogServiceImpl workLogService;
    @Autowired
    private WorkLogMapper workLogMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private ISysParamAPI iSysParamAPI;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(JobExecutionContext context) throws JobExecutionException {

        log.info("******正在生成晚班工作日志...******");
        autoAdd();
        log.info("******晚班工作日志生成完成！*******");
    }


    @Transactional(rollbackFor = Exception.class)
    public void execute() {
        log.info("******正在生成晚班工作日志...******");
        autoAdd();
        log.info("******晚班工作日志生成完成！*******");
    }

    /**
     * 每天早上8点生成晚班工作日志
     */
    private void autoAdd() {
        Date date = new Date();
        List<SysDepartModel> allSysDepart = iSysBaseAPI.getAllSysDepart();
        if (CollUtil.isNotEmpty(allSysDepart)) {
            for (SysDepartModel sysDepartModel : allSysDepart) {
                WorkLog depot = new WorkLog();
                depot.setOrgId(sysDepartModel.getId());
                depot.setCreateTime(date);
                String logCode = workLogService.generateLogCode();
                depot.setCode(logCode);

                depot.setStatus(0);
                depot.setConfirmStatus(0);
                depot.setCheckStatus(0);

                //工作内容赋值
                depot.setIsEmergencyDisposal(0);
                depot.setIsDocumentPublicity(0);

                SysParamModel schedule = iSysParamAPI.selectByCode(SysParamCodeConstant.WORK_SCHEDULE);
                SysParamModel note = iSysParamAPI.selectByCode(SysParamCodeConstant.WORK_NOTE);
                depot.setNote(note.getValue());
                depot.setSchedule(schedule.getValue());

                depot.setLogTime(DateUtil.parse(DateUtil.today(), "yyyy-MM-dd"));
                depot.setDelFlag(0);
                workLogMapper.insert(depot);
                //1为白班，2为晚班
                workLogService.sendMessage(sysDepartModel.getId(),date,2);
            }
        }

    }
}
