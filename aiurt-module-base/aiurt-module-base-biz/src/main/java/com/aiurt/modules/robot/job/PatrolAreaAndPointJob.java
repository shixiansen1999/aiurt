package com.aiurt.modules.robot.job;

import com.aiurt.modules.robot.service.IPatrolAreaInfoService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.*;

import javax.annotation.Resource;

/**
 * @author wgp
 * @Title:
 * @Description: 定时任务同步点位和巡检区域
 * @date 2022/10/810:03
 */
@Slf4j
public class PatrolAreaAndPointJob implements Job {
    @Resource
    private IPatrolAreaInfoService patrolAreaInfoService;

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
        log.info("PatrolAreaAndPointJob类定时任务同步点位和巡检区域基础数据，时间：{}", DateUtils.getTimestamp());
        patrolAreaInfoService.synchronizeAreaAndPoint();
    }
}
