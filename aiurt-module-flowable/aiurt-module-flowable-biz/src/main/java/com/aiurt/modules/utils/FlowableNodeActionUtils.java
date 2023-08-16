package com.aiurt.modules.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.HttpContextUtils;
import com.aiurt.common.util.SafeSqlCheckerUtils;
import com.aiurt.common.util.TokenUtils;
import com.aiurt.config.sign.util.HttpUtils;
import com.aiurt.modules.common.constant.FlowModelExtElementConstant;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.state.entity.ActCustomState;
import com.aiurt.modules.state.service.IActCustomStateService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

        String stateValue = node.getString(FlowModelExtElementConstant.STATE_UPDATE);
        String customInterfaceAddress = node.getString(FlowModelExtElementConstant.CUSTOM_INTERFACE);
        String customSql = node.getString(FlowModelExtElementConstant.CUSTOM_SQL);

        // 更新流程状态
        updateProcessState(processInstanceId, stateValue);

        // 执行自定义接口
        executeCustomInterface(customInterfaceAddress, processInstanceId);

        // 执行自定义sql
        executeCustomSql(customSql);
    }

    /**
     * 执行自定义sql
     *
     * @param customSql
     */
    private static void executeCustomSql(String customSql) {
        if (StrUtil.isEmpty(customSql)) {
            return;
        }

        boolean safeSql = SafeSqlCheckerUtils.isSafeSql(customSql);
        if (!safeSql) {
            log.error("流程节点自定义sql执行失败，sql中只能包含update或者select语句");
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
        }
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

}
