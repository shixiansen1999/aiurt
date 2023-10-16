package com.aiurt.modules.remind.cmd;

import com.aiurt.modules.remind.job.TimeOutRemindJobHandler;
import com.alibaba.fastjson.JSONObject;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.job.api.Job;
import org.flowable.job.service.TimerJobService;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 自定义定时任务cmd
 * @author fgw
 */
public class CustomJobCmd implements Command<Object>, Serializable {

    /**
     * 流程实例id
     */
    protected String processInstanceId;

    /**
     * 任务id
     */
    protected String taskId;

    /**
     * 执行实例id
     */
    protected String executionId;

    /**
     * 执行时间
     */
    protected Date dueDate;


    /**
     * 备注
     */
    protected String comment;



    public CustomJobCmd(String processInstanceId, String comment, String taskId, String executionId, Date dueDate) {
        this.processInstanceId = processInstanceId;
        this.comment = comment;
        this.taskId = taskId;
        this.executionId = executionId;
        this.dueDate = dueDate;
    }


    @Override
    public Object execute(CommandContext commandContext) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comment", this.comment);
        jsonObject.put("task_id", this.taskId);

        TimerJobService timerJobService = CommandContextUtil.getTimerJobService(commandContext);
        TimerJobEntity job = timerJobService.createTimerJob();
        job.setJobType(Job.JOB_TYPE_TIMER);
        job.setExclusive(true);
        // 作业处理器类型
        job.setJobHandlerType(TimeOutRemindJobHandler.TYPE);
        // 处理时间
        job.setDuedate(this.dueDate);
        job.setExecutionId(this.executionId);
        job.setProcessInstanceId(this.processInstanceId);
        job.setJobHandlerConfiguration(jsonObject.toJSONString());
        timerJobService.scheduleTimerJob(job);
        return null;

    }
}
