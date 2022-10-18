package com.aiurt.modules.robot.job;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.robot.entity.RobotInfo;
import com.aiurt.modules.robot.service.IRobotInfoService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description: 机器人现场基础数据定时同步任务
 * @date 2022/10/810:03
 */
@Slf4j
public class RobotInfoJob implements Job {

    @Resource
    private IRobotInfoService robotInfoService;

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
        // 查询数据库存在的机器人数据
        List<RobotInfo> robotInfos = robotInfoService.getBaseMapper().selectList(null);

        log.info("RobotInfoJob类定时任务同步机器人基础数据，时间：{}，机器人列表：{}", DateUtils.getTimestamp(),robotInfos);

        // 同步机器人数据
        robotInfoService.synchronizeRobotData(CollUtil.isNotEmpty(robotInfos) ? robotInfos : CollUtil.newArrayList());
    }
}
