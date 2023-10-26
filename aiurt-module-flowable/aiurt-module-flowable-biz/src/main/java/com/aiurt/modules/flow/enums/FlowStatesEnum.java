package com.aiurt.modules.flow.enums;


public enum FlowStatesEnum {

    /**
     *  待发起
     */
    UN_COMPLETE(1, "待发起"),

    /**
     * 进行中
     */
    IN_PROGRESS(2, "进行中"),

    /**
     * 已退回
     */
    RETURN(3, "已退回"),

    /**
     * 已终止
     */
    TERMINATED(4, "已终止"),

    /**
     * 已作废
     */
    CANCEL(5, "已作废"),

    /**
     * 已归档
     */
    COMPLETE(6, "已归档");

    private Integer code;
    private String message;

    private FlowStatesEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }


    public static FlowStatesEnum getByCode(Integer code) {
        for (FlowStatesEnum flowStatesEnum : FlowStatesEnum.values()) {
            if (flowStatesEnum.getCode().equals(code)) {
                return flowStatesEnum;
            }
        }
        return null;
    }
}
