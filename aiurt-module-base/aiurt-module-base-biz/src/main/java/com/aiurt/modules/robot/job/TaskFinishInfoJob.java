package com.aiurt.modules.robot.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.aiurt.modules.robot.service.ITaskFinishInfoService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author JB
 * @Description: 定时任务同步巡视任务和任务的巡视记录
 */
@Slf4j
public class TaskFinishInfoJob implements Job {
    @Resource
    private ITaskFinishInfoService taskFinishInfoService;

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
     * fires that is associated with the <code>Job</code>.
     * </p>
     *
     * <p>
     * The implementation may wish to set a
     * {@link JobExecutionContext#setResult(Object) result} object on the
     * {@link JobExecutionContext} before this method exits.  The result itself
     * is meaningless to Quartz, but may be informative to
     * <code>{@link JobListener}s</code> or
     * <code>{@link TriggerListener}s</code> that are watching the job's
     * execution.
     * </p>
     *
     * @param context
     * @throws JobExecutionException if there is an exception while executing the job.
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("TaskFinishInfoJob类定时任务同步巡视任务和任务的巡视记录，时间：{}", DateUtils.getTimestamp());
        Date endTime = new Date();
        DateTime startTime = DateUtil.offsetHour(endTime, -1);
        taskFinishInfoService.synchronizeRobotTask(startTime, endTime);
    }
}
