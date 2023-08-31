package com.aiurt.common.exception;

/**
 * @Description: 自定义返回值
 * @author: aiurt
 */
public enum AiurtErrorEnum {

    /**
     * 故障上报
     */
    NEW_FAULT(1, "故障上报"),

    /***
     * 基础模块 code 1开头 五位数
     */

    /**
     * 6 开头
     */
    FLOW_MODEL_NOT_FOUND(61001,"流程模板信息不存在"),
    FLOW_DEFINITION_NOT_FOUND(61002,"流程定义信息不存在, 请先发布流程！"),
    FLOW_TASK_NOT_FOUND(61003,"请重新配置流程, 该流程配置错误, 无法找到第一个用户任务"),
    TASK_ID_NOT_FOUND(61004, "活动(%s)不存在或已结束"),
    MULTI_INSTANCE_USER_NULL(61005, "执行多实例任务时，选择的人员不能为空！"),
    PROCESS_INSTANCE_NOT_FOUND(61006, "流程实例不存在！"),
    PROCESS_INSTANCE_IS_DELETE(61007, "该流程实例已者不存在或被删除"),
    NEXT_NODE_NOT_FOUND(61008, "当前配置异常(%s)，请联系管理员处理！"),
    /**
     * 7 开头
     */
    INVALID_DATA_FIELD(71001,"数据验证失败，无效的数据实体对象字段！");

    private Integer code;
    private String message;

    private AiurtErrorEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }


    public static AiurtErrorEnum getByCode(Integer code) {
        for (AiurtErrorEnum aiurtErrorEnum : AiurtErrorEnum.values()) {
            if ( aiurtErrorEnum.getCode().equals(code)) {
                return aiurtErrorEnum;
            }
        }
        return null;
    }
}
