package com.aiurt.boot.pool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.drools.entity.DroolsRule;
import com.aiurt.boot.drools.service.IDroolsRuleService;
import com.aiurt.boot.drools.util.DroolsUtil;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.PatrolTaskStationMapper;
import com.aiurt.boot.task.mapper.PatrolTaskUserMapper;
import com.aiurt.boot.task.service.IPatrolTaskOrganizationService;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.boot.task.service.IPatrolTaskStandardService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.kie.api.runtime.KieSession;
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
 * 巡检任务漏检定时检测，使用drools的规则引擎判断是否漏检
 *
 * @author cgkj0
 */
@Slf4j
@Component
public class PatrolTaskMissingDetectionByDrools implements Job {

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
    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
    @Autowired
    private IDroolsRuleService droolsRuleService;
    @Autowired
    private IPatrolTaskStandardService patrolTaskStandardService;

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(JobExecutionContext context) throws JobExecutionException {
        taskDetection();
    }

    public void execute() throws Exception {
        taskDetection();
    }

    /**
     * 周一和周五0点检测漏检的任务
     */
    private void taskDetection() throws Exception {
        DroolsRule patrolTaskOmitRule = droolsRuleService.queryByName("patrol_task_omit_rule");
        KieSession kieSession = DroolsUtil.reload(patrolTaskOmitRule.getRule());

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
//            Date patrolDate = l.getPatrolDate();
//            if (ObjectUtil.isNotEmpty(l.getEndTime())) {
//                String endTime = DateUtil.format(l.getEndTime(), "HH:mm:ss");
//                patrolDate = DateUtil.parse(DateUtil.format(patrolDate, "yyyy-MM-dd " + endTime));
//            }
//            // 当前时间
//            Date now = new Date();
//            int compare = DateUtil.compare(now, patrolDate);
//            if (compare >= 0) {
//                l.setOmitStatus(PatrolConstant.OMIT_STATUS);
            // 这里使用drools规则判断是否是漏检
            LambdaQueryWrapper<PatrolTaskStandard> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PatrolTaskStandard::getTaskId, l.getId());
            queryWrapper.eq(PatrolTaskStandard::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<PatrolTaskStandard> list = patrolTaskStandardService.list(queryWrapper);
            PatrolTaskStandard patrolTaskStandard = null;
            if (list.size() > 0) {
                patrolTaskStandard = list.get(0); // 使用到patrolTaskStandard是因为里面有专业Code
            }
            // 注入fact对象
            kieSession.insert(patrolTaskStandard);
            kieSession.insert(l);
            // 执行规则
            kieSession.fireAllRules();

            if (l.getOmitStatus().equals(PatrolConstant.OMIT_STATUS)) {
                missNum.getAndAdd(1);
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
                    try {
                        TodoDTO todoDTO = new TodoDTO();
                        todoDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                        todoDTO.setTitle("巡视任务-漏检"+DateUtil.today());
                        todoDTO.setMsgAbstract("巡视任务-漏检");
                        todoDTO.setPublishingContent("巡视任务漏检，请尽快处置");
                        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_MESSAGE_PROCESS);
                        todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                        //构建消息模板
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("code",l.getCode());
                        map.put("patrolTaskName",l.getName());
                        List<String>  station = patrolTaskStationMapper.getStationByTaskCode(l.getCode());
                        map.put("patrolStation", CollUtil.join(station,","));
                        if (ObjectUtil.isNotEmpty(l.getStartTime()) && ObjectUtil.isNotEmpty(l.getEndTime())) {
                            String date = DateUtil.format(l.getPatrolDate(), "yyyy-MM-dd");
                            map.put("patrolTaskTime",date+" "+DateUtil.format(l.getStartTime(),"HH:mm")+"-"+date+" "+DateUtil.format(l.getEndTime(),"HH:mm"));
                        }
                        QueryWrapper<PatrolTaskUser> wrapper = new QueryWrapper<>();
                        wrapper.lambda().eq(PatrolTaskUser::getTaskCode, l.getCode()).eq(PatrolTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0);
                        List<PatrolTaskUser> taskUsers = patrolTaskUserMapper.selectList(wrapper);
                        if (CollectionUtil.isNotEmpty(taskUsers)) {
                            String[] userIds = taskUsers.stream().map(PatrolTaskUser::getUserId).toArray(String[]::new);
                            List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                            String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                            map.put("patrolName", realNames);

                        }
                        todoDTO.setData(map);
                        todoDTO.setProcessDefinitionName("巡视管理");
                        todoDTO.setTaskName(l.getName() + "(漏巡待处理)");
                        todoDTO.setBusinessKey(l.getId());
                        todoDTO.setBusinessType(TodoBusinessTypeEnum.PATROL_OMIT.getType());
                        todoDTO.setCurrentUserName(userName);
                        todoDTO.setTaskType(TodoTaskTypeEnum.PATROL.getType());
                        todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
                        todoDTO.setTimedTask(true);
                        isTodoBaseApi.createTodoTask(todoDTO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        log.info("存在{}条任务记录漏检,并更新为已漏检状态！", missNum.get());
        kieSession.dispose();  // 关闭会话
    }
}
