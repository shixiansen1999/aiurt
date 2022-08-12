package com.aiurt.modules.worklog.service;

import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.worklog.dto.WorkLogRemindDTO;
import com.aiurt.modules.worklog.entity.WorkLogRemind;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IWorkLogRemindService extends IService<WorkLogRemind> {

    /**
     * 添加工作日志提醒时间设置
     * @param dto
     * @param req
     * @return
     */
    Result add (WorkLogRemindDTO dto, HttpServletRequest req);

    /**
     * 获取当登陆人所在班组日志提醒信息
     * @param req
     * @return
     */
    WorkLogRemind getWorkLogRemind(HttpServletRequest req);
    /**
     * 根据当前时间和组织，查询当天部门上班的人员
     * @param dateNow
     * @param orgId
     * @return
     */
    List<ScheduleRecord> getOrgUserTodayWork(String dateNow, String orgId);
}
