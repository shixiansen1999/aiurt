package com.aiurt.modules.common.constant;


/**
 *
 *  流程ext 常量
 * @author fgw
 *
 */
public interface FlowModelExtElementConstant {

    /**
     * 选人 <flowable:userassignee name="actor_submit_candidate" value=“json”  alias="别称"/>
     */
    String EXT_USER_ASSIGNEE = "userassignee";

    /**
     * 选人 <flowable:userassignee name="actor_submit_candidate" value=“json”  alias="别称"/>
     */
    String EXT_USER_NAME = "name";

    /**
     * 选人 <flowable:userassignee name="actor_submit_candidate" value=“json”  alias="别称"/>
     */
    String EXT_USER_VALUE = "value";

    /**
     * 选人 <flowable:userassignee name="actor_submit_candidate" value=“json”  alias="别称"/>
     */
    String EXT_USER_ALIAS = "alias";


    /**
     * 抄送人标签
     */
    String EXT_CARBON_COPY = "carboncopy";

    /**
     * 自动选人
     */
    String EXT_AUTO_SELECT = "autoselect";

    /**
     * 多人审批规则
     */
    String EXT_MULTI_APPROVAL_RULE = "multiApprovalRule";
    /**
     * 节点前附加操作
     */
    String EXT_PRE_NODE_ACTION = "preNodeAction";
    /**
     * 节点后附加操作
     */
    String EXT_POST_NODE_ACTION = "postNodeAction";

    /**
     * 从流程实例的变量中获取状态更新标志
     */
    String STATE_UPDATE = "stateUpdate";
    /**
     * 从流程实例的变量中获取自定义接口信息
     */
    String CUSTOM_INTERFACE = "customInterface";
    /**
     * 从流程实例的变量中获取自定义 SQL 语句
     */
    String CUSTOM_SQL = "customSql";
    /**
     * 表单字段在节点上的配置
     */
    String FORM_FIELD_CONFIG = "formFieldConfig";
}
