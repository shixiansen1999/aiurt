package com.aiurt.modules.common.constant;

/**
 *  流程模板属性
 * @author fgw
 * @date 2022-10-21 12:12
 */
public interface FlowModelAttConstant {

    String FLOWABLE = "flowable";

    /**
     * 页面类型1：静态， 0：动态
     */
    String FORM_TYPE = "formType";

    /**
     * 人员类型
     */
    String DATA_TYPE = "dataType";

    /**
     * 表单设计器
     */
    String FORM_DYNAMIC_URL = "formDynamicUrl";

    /**
     * 表单url
     */
    String FORM_URL = "formUrl";

    /**
     * 业务处理
     */
    String SERVICE = "service";

    /**
     * 流程变量
     */
    String FORM_TASK_VARIABLES = "formtaskVariables";

    /**
     * 业务表单
     */
    String STATIC_FORM_TYPE = "1";

    /**
     * 表单类型0， 表单设计器，动态
     */
    String DYNAMIC_FORM_TYPE = "0";

    /**
     * 角色配置
     */
    String ROLE = "role";

    /**
     * 机构
     */
    String DEPT = "dept";

    /**
     * 用户
     */
    String USER = "user";

    /**
     * 办理人类型
     */
    String DYNAMIC_PERSON = "dynamicPerson";

    /**
     * 页面按钮属性
     */
    String FORM_OPERATION = "formOperation";

    /**
     * 页面按钮id
     */
    String ID = "id";

    /**
     * 页面按钮名称
     */
    String LABEL = "label";

    /**
     * 页面按钮类型 agree save
     */
    String TYPE = "type";

    /**
     * 页面按钮排序
     */
    String SHOW_ORDER = "showOrder";

    /**
     * 备注是否显示：1：是，0否
     */
    String IS_DISPLAY_REMARK = "isDisplayRemark";

    /**
     * 备注是否必填： 1：是，0否
     */
    String IS_REQUIRE_REMARK = "isRequireRemark";

    /**
     * 选人类型
     */
    String USER_TYPE = "userType";

    /**
     * 流程终止接口变量名
     */
    String CANCEL = "cancel";

    // =================流转条件=======================

    String FLOW_CONDITION = "flowCondition";

    String NAME = "name";

    String CODE = "code";

    String CONDITION = "condition";

    String VALUE = "value";

    String RELATION = "relation";
}
