package com.aiurt.modules.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.HttpContextUtils;
import com.aiurt.common.util.SafeSqlCheckerUtils;
import com.aiurt.common.util.SpringContextHolder;
import com.aiurt.common.util.TokenUtils;
import com.aiurt.config.sign.util.HttpUtils;
import com.aiurt.modules.common.constant.FlowModelExtElementConstant;
import com.aiurt.modules.common.constant.FlowVariableConstant;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import com.aiurt.modules.state.entity.ActCustomState;
import com.aiurt.modules.state.service.IActCustomStateService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author:wgp
 * @create: 2023-08-15 16:19
 * @Description: 流程节点前后操作工具类
 */
@Slf4j
public class FlowableNodeActionUtils {
    /**
     * 业务关键字,比如业务id
     */
    private static final String PARAM_BUSINESS_KEY = "businessKey";

    /**
     * 模型关键字
     */
    private static final String PARAM_MODEL_KEY = "modelKey";

    /**
     * 用户名
     */
    private static final String PARAM_USERNAME = "username";

    /**
     * 用户ID
     */
    private static final String PARAM_USER_ID = "userId";

    /**
     * 原因
     */
    private static final String PARAM_REASON = "reason";

    /**
     * 评论
     */
    private static final String VARIABLE_COMMENT = "comment";


    public static void processTaskData(TaskEntity taskEntity, String processDefinitionId, String taskDefinitionKey, String processInstanceId, String nodeAction) {
        String taskId = taskEntity.getId();
        TaskService taskService = SpringContextUtils.getBean(TaskService.class);


        // 如果是节点后操作， 只在最后一个任务提交执行
        if (StrUtil.equalsIgnoreCase(nodeAction, FlowModelExtElementConstant.EXT_POST_NODE_ACTION)) {
            IMultiInTaskService multiInTaskService = SpringContextUtils.getBean(IMultiInTaskService.class);
            Boolean completeTask = multiInTaskService.isCompleteTask(taskEntity);
            if (completeTask) {
                processTaskData(processDefinitionId, taskDefinitionKey, processInstanceId, nodeAction);

                // 提交则需要删除变量， 否则回退时不执行
                taskService.removeVariable(taskId, FlowModelExtElementConstant.EXT_PRE_NODE_ACTION + "_" + taskDefinitionKey);

                // 删除加签的变量
                taskService.removeVariable(taskId, FlowVariableConstant.ADD_ASSIGNEE_LIST + taskDefinitionKey);
            }
        } else {
            // 判断是否已经执行
            Boolean variableLocal = taskService.getVariable(taskId, nodeAction + "_" + taskDefinitionKey, Boolean.class);
            if (Objects.nonNull(variableLocal) && variableLocal) {
                return;
            }
            processTaskData(processDefinitionId, taskDefinitionKey, processInstanceId, nodeAction);
            // 变量， 标识已经执行
            taskService.setVariable(taskId, nodeAction + "_" + taskDefinitionKey, Boolean.TRUE);
        }
    }

    /**
     * 处理流程任务数据，包括更新流程状态、执行自定义接口、执行自定义SQL等操作。
     *
     * @param processDefinitionId 流程定义ID
     * @param taskDefinitionKey   任务定义Key
     * @param processInstanceId   流程实例ID
     * @param processInstanceId   节点前后的判断标识
     */
    public static void processTaskData(String processDefinitionId, String taskDefinitionKey, String processInstanceId, String nodeAction) {
        IActCustomTaskExtService actCustomTaskExtService = SpringContextUtils.getBean(IActCustomTaskExtService.class);
        ActCustomTaskExt actCustomTaskExt = actCustomTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskDefinitionKey);

        if (ObjectUtil.isEmpty(actCustomTaskExt)) {
            return;
        }

        JSONObject node = getNodeActionConfig(nodeAction, actCustomTaskExt);
        if (Objects.isNull(node)) {
            return;
        }

        String stateValue = node.getString(FlowModelExtElementConstant.STATE_UPDATE);
        String customInterfaceAddress = node.getString(FlowModelExtElementConstant.CUSTOM_INTERFACE);
        String customSql = node.getString(FlowModelExtElementConstant.CUSTOM_SQL);

        // 获取业务id
        FlowApiService flowApiService = SpringContextHolder.getBean(FlowApiService.class);
        ProcessInstance processInstance = flowApiService.getProcessInstance(processInstanceId);
        String businessId = getBusinessIdByProcessInstanceId(processInstanceId);

        // 更新流程状态
        updateProcessState(processInstanceId, stateValue);

        // 执行自定义接口
        executeCustomInterface(customInterfaceAddress, processInstanceId);

        // 执行自定义sql
        executeCustomSql(customSql, businessId);
    }

    /**
     * 执行自定义sql
     *
     * @param customSql  自定义sql
     * @param businessId 业务数据id
     */
    private static void executeCustomSql(String customSql, String businessId) {
        // 默认给sql拼接业务id
        String sql = buildCustomUpdateSql(customSql, businessId);

        if (StrUtil.isEmpty(sql)) {
            return;
        }

        DataSource dataSource = SpringContextUtils.getBean(DataSource.class);
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            // 执行SQL语句
            boolean success = statement.execute(customSql);
            if (!success) {
                log.error("流程节点自定义sql执行失败: {}", customSql);
            }
        } catch (SQLException e) {
            log.error("流程节点自定义sql执行失败: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行自定义接口
     *
     * @param customInterfaceAddress 接口地址
     * @param processInstanceId      流程实例id
     */
    private static void executeCustomInterface(String customInterfaceAddress, String processInstanceId) {
        if (StrUtil.isEmpty(customInterfaceAddress)) {
            return;
        }

        Map<String, String> headers = buildHeaders();
        String responseResult = HttpUtils.sendGetRequest(customInterfaceAddress, headers, buildRequestParams(processInstanceId));
        log.info(responseResult);
    }

    /**
     * 更新流程实例状态
     *
     * @param processInstanceId 流程实例id
     * @param stateValue        新的状态值
     */
    private static void updateProcessState(String processInstanceId, String stateValue) {
        if (StrUtil.isEmpty(stateValue)) {
            return;
        }
        IActCustomStateService actCustomStateService = SpringContextUtils.getBean(IActCustomStateService.class);
        ActCustomState actCustomState = actCustomStateService.getCustomStateByProcessInstanceId(processInstanceId);
        if (ObjectUtil.isNotEmpty(actCustomState)) {
            actCustomState.setState(stateValue);
            actCustomStateService.updateById(actCustomState);
        } else {
            ActCustomState addActCustomState = new ActCustomState();
            addActCustomState.setProcessInstanceId(processInstanceId);
            addActCustomState.setState(stateValue);
            actCustomStateService.save(addActCustomState);
        }
    }

    /**
     * 给自定义SQL拼接业务id条件。是update或者delete语句必须要拼接条件
     *
     * @param customSql  自定义SQL。
     * @param businessId 业务ID或标识，并作为更新条件。
     * @return 构建的SQL语句，如果自定义SQL不安全或不是UPDATE语句，则返回null。
     */
    private static String buildCustomUpdateSql(String customSql, String businessId) {
        if (StrUtil.isEmpty(customSql)) {
            return null;
        }

        boolean safeSql = SafeSqlCheckerUtils.isSafeSql(customSql);
        if (!safeSql) {
            log.error("流程节点自定义SQL安全检查失败！");
            return null;
        }

        // 判断是否为 UPDATE 或者 DELETE 语句，如果是两者之一，需要拼接条件
        if (customSql.trim().toLowerCase().startsWith(SafeSqlCheckerUtils.SQL_UPDATE)
                || customSql.trim().toLowerCase().startsWith(SafeSqlCheckerUtils.SQL_DELETE)) {
            if (StrUtil.isEmpty(businessId)) {
                log.error("流程节点自定义SQL拼接失败，SQL是update或者delete语句时，业务id为空");
                return null;
            }

            StringBuilder sqlBuilder = new StringBuilder(customSql);

            // 查找 WHERE 关键字的位置
            int whereIndex = customSql.toLowerCase().indexOf("where");
            if (whereIndex != -1) {
                // 找到 WHERE 关键字，检查是否是空的 WHERE
                if (whereIndex + 5 == customSql.length()) {
                    // WHERE 后面是空的，直接添加条件
                    sqlBuilder.append(" id = '").append(businessId).append("'");
                } else {
                    // 在 WHERE 关键字后追加条件
                    sqlBuilder.insert(whereIndex + 5, " id = '" + businessId + "' AND ");
                }
            } else {
                // 未找到 WHERE 关键字，添加 WHERE 条件
                sqlBuilder.append(" WHERE id = '").append(businessId).append("'");
            }

            return sqlBuilder.toString();
        }

        return customSql;
    }

    /**
     * 构建请求头信息
     *
     * @return
     */
    private static Map<String, String> buildHeaders() {
        Map<String, String> headers = CollUtil.newHashMap();
        headers.put(CommonConstant.X_ACCESS_TOKEN, TokenUtils.getTokenByRequest(HttpContextUtils.getHttpServletRequest()));
        return headers;
    }

    /**
     * 构建请求参数
     *
     * @param processInstanceId 流程实例ID
     * @return 构建的请求参数
     */
    private static Map<String, String> buildRequestParams(String processInstanceId) {
        ProcessInstance processInstance = ProcessEngines.getDefaultProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        Map<String, String> params = CollUtil.newHashMap();
        params.put(PARAM_BUSINESS_KEY, processInstance.getBusinessKey());
        params.put(PARAM_MODEL_KEY, processInstance.getProcessDefinitionKey());

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.nonNull(loginUser)) {
            params.put(PARAM_USERNAME, loginUser.getUsername());
            params.put(PARAM_USER_ID, loginUser.getId());
        }

        String comment = ProcessEngines.getDefaultProcessEngine().getRuntimeService().getVariable(processInstance.getProcessInstanceId(), VARIABLE_COMMENT, String.class);
        params.put(PARAM_REASON, comment);

        return params;
    }


    /**
     * 获取节点操作配置信息。
     *
     * @param nodeAction                     节点操作类型，可以是 FlowModelExtElementConstant.EXT_PRE_NODE_ACTION 或 FlowModelExtElementConstant.EXT_POST_NODE_ACTION。
     * @param byProcessDefinitionIdAndTaskId 包含节点操作配置的对象。
     * @return 节点操作配置的 JSON 对象，如果没有匹配的配置则返回 null。
     */
    private static JSONObject getNodeActionConfig(String nodeAction, ActCustomTaskExt byProcessDefinitionIdAndTaskId) {
        JSONObject nodeConfig = null;

        if (FlowModelExtElementConstant.EXT_PRE_NODE_ACTION.equals(nodeAction)) {
            nodeConfig = byProcessDefinitionIdAndTaskId.getPreNodeAction();
        } else if (FlowModelExtElementConstant.EXT_POST_NODE_ACTION.equals(nodeAction)) {
            nodeConfig = byProcessDefinitionIdAndTaskId.getPostNodeAction();
        }

        return nodeConfig;
    }

    /**
     * 根据流程实例ID获取关联的业务ID。
     *
     * @param processInstanceId 流程实例ID
     * @return 关联的业务ID，如果流程实例不存在或关联的业务ID为空，则返回空字符串
     */
    public static String getBusinessIdByProcessInstanceId(String processInstanceId) {
        FlowApiService flowApiService = SpringContextHolder.getBean(FlowApiService.class);
        ProcessInstance processInstance = flowApiService.getProcessInstance(processInstanceId);
        String businessId = "";

        if (ObjectUtil.isNotEmpty(processInstance)) {
            businessId = processInstance.getBusinessKey();
        }

        return businessId;
    }

    public static void main(String[] args) {
        // 测试安全的UPDATE语句
        String safeUpdateSql = "UPDATE users SET name = 'John' WHERE id = 1";
        String safeResult = buildCustomUpdateSql(safeUpdateSql, "1");
        System.out.println("Safe Update SQL Result: " + safeResult);

        // 测试包含无效条件的SQL语句
        String invalidConditionSql = "DELETE FROM orders WHERE 1=1 OR 2=2";
        String invalidConditionResult = buildCustomUpdateSql(invalidConditionSql, "1");
        System.out.println("Invalid Condition SQL Result: " + invalidConditionResult);

        // 测试其他SQL语句
        String otherSql = "INSERT INTO customers (name, email) VALUES ('Alice', 'alice@example.com')";
        String otherResult = buildCustomUpdateSql(otherSql, "1");
        System.out.println("Other SQL Result: " + otherResult);

        // 测试未提供业务id的情况
        String missingBusinessIdSql = "UPDATE products SET price = 10 WHERE id = 5";
        String missingBusinessIdResult = buildCustomUpdateSql(missingBusinessIdSql, null);
        System.out.println("Missing Business ID SQL Result: " + missingBusinessIdResult);

        // 测试长字符串SQL语句
        String longSql = "DELETE FROM orders WHERE " + "id = '1'";
        String result5 = buildCustomUpdateSql(longSql, "ceshi");
        System.out.println("Long SQL Result: " + result5);
    }

}
