package com.aiurt.modules.robot.job;

import com.aiurt.modules.robot.service.ITaskPathInfoService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.*;

import javax.annotation.Resource;

/**
 * @author wgp
 * @Title:
 * @Description: 定时任务同步任务模板
 * @date 2022/10/810:40
 */
@Slf4j
public class TaskPathInfoJob implements Job {
    @Resource
    private ITaskPathInfoService taskPathInfoService;

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
        log.info("TaskPathInfoJob类定时任务同步任务模板基础数据，时间：{}", DateUtils.getTimestamp());
        taskPathInfoService.synchronizeTaskPathInfo();
    }
}
