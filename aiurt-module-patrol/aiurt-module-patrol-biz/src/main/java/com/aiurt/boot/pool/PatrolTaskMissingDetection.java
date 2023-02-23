package com.aiurt.boot.pool;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.aiurt.boot.task.service.IPatrolTaskOrganizationService;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.modules.todo.dto.TodoDTO;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.SysParamModel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 巡检任务漏检定时检测
 *
 * @author cgkj0
 */
@Slf4j
@Component
public class PatrolTaskMissingDetection implements Job {

    @Autowired
    private IPatrolTaskService patrolTaskService;
    @Autowired
    private ISTodoBaseAPI isTodoBaseApi;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IPatrolTaskOrganizationService patrolTaskOrganizationService;
    @Autowired
    private ISysParamAPI iSysParamAPI;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(JobExecutionContext context) throws JobExecutionException {
        taskDetection();
    }

    public void execute() {
        taskDetection();
    }

    /**
     * 周一和周五0点检测漏检的任务
     */
    private void taskDetection() {

        // 获取以下状态为0待指派、1待确认、2待执行、3已退回、4执行中的任务
        List<Integer> status = Arrays.asList(PatrolConstant.TASK_INIT, PatrolConstant.TASK_CONFIRM,
                PatrolConstant.TASK_EXECUTE, PatrolConstant.TASK_RETURNED, PatrolConstant.TASK_RUNNING);
        List<PatrolTask> taskList = Optional.ofNullable(
                patrolTaskService.lambdaQuery()
                        .in(PatrolTask::getStatus, status)
                        .eq(PatrolTask::getOmitStatus, PatrolConstant.UNOMIT_STATUS)
                        .list()
        ).orElseGet(Collections::emptyList);
        if (CollectionUtil.isEmpty(taskList)) {
            return;
        }
        List<String> taskCodes = taskList.stream().map(PatrolTask::getCode).collect(Collectors.toList());
        List<PatrolTaskOrganization> orgList = patrolTaskOrganizationService.lambdaQuery()
                .eq(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(PatrolTaskOrganization::getTaskCode, taskCodes)
                .list();

        Map<String, List<PatrolTaskOrganization>> orgCodesMap = orgList.stream()
                .filter(l -> StrUtil.isNotEmpty(l.getTaskCode()))
                .collect(Collectors.groupingBy(PatrolTaskOrganization::getTaskCode));

        // 统计漏检数
        AtomicInteger missNum = new AtomicInteger();

//        List<LoginUser> users = sysBaseApi.getUserByRoleCode("String roleCode");

        taskList.stream().forEach(l -> {
            if (null == l.getPatrolDate()) {
                return;
            }
            Date patrolDate = l.getPatrolDate();
            if (ObjectUtil.isNotEmpty(l.getEndTime())) {
                String endTime = DateUtil.format(l.getEndTime(), "HH:mm:ss");
                patrolDate = DateUtil.parse(DateUtil.format(patrolDate, "yyyy-MM-dd " + endTime));
            }
            // 当前时间
            Date now = new Date();
            int compare = DateUtil.compare(now, patrolDate);
            if (compare >= 0) {
                l.setOmitStatus(PatrolConstant.OMIT_STATUS);
                boolean update = patrolTaskService.updateById(l);
                if (update) {
                    missNum.getAndAdd(1);
                }
                // 发送待办消息
                List<PatrolTaskOrganization> organizations = Optional.ofNullable(orgCodesMap.get(l.getCode())).orElseGet(ArrayList::new);
                List<String> orgCodes = organizations.stream().map(PatrolTaskOrganization::getOrgCode).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(orgCodes)) {
                    String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(orgCodes, Arrays.asList(RoleConstant.FOREMAN));
                    if(StrUtil.isEmpty(userName)){
                        return;
                    }
                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                    todoDTO.setTitle("巡视任务-漏检");
                    todoDTO.setMsgAbstract("巡视任务-漏检");
                    todoDTO.setPublishingContent("巡视任务漏检，请尽快处置");
                    SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_MESSAGE_PROCESS);
                    todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");

                    todoDTO.setProcessDefinitionName("巡视管理");
                    todoDTO.setTaskName(l.getName() + "(漏巡待处理)");
                    todoDTO.setBusinessKey(l.getId());
                    todoDTO.setBusinessType(TodoBusinessTypeEnum.PATROL_OMIT.getType());
                    todoDTO.setCurrentUserName(userName);
                    todoDTO.setTaskType(TodoTaskTypeEnum.PATROL.getType());
                    todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
                    todoDTO.setTimedTask(true);
                    isTodoBaseApi.createTodoTask(todoDTO);
                }
            }
        });
        log.info("存在{}条任务记录漏检,并更新为已漏检状态！", missNum.get());
    }
}
