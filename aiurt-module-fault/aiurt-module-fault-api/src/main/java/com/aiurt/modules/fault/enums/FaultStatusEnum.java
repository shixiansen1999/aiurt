package com.aiurt.modules.fault.enums;

import cn.hutool.core.util.StrUtil;

public enum  FaultStatusEnum {

    /**
     * 故障上报,待审批
     */
    NEW_FAULT("1", "故障上报", 1),

    /**
     *审批驳回
     */
    APPROVAL_REJECT("2", "审批已驳回", 2),

    /**
     * 审批通过，待指派
     */
    APPROVAL_PASS("3", "审批通过",3),

    /**
     * 已指派
     */
    ASSIGN("4","指派",4),

    /**
     * 已领取
     */
    RECEIVE("5","领取",5),

    /**
     * 接收指派
     */
    RECEIVE_ASSIGN("6","已接收",6),

    /**
     * 维修中
     */
    REPAIR("7","维修中",7),

    /**
     * 发起挂起
     */
    HANGUP_REQUEST("9","挂起审核",9),

    /**
     * 挂起
     */
    HANGUP("10","挂起",10),

    /**
     * 作废
     */
    CANCEL("0","作废",0);




    private String code;
    private String message;
    private Integer status;

    private FaultStatusEnum(String code, String message, Integer status){
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }


    public static FaultStatusEnum getByCode(String code) {
        for (FaultStatusEnum faultStatusEnum : FaultStatusEnum.values()) {
            if (StrUtil.equalsIgnoreCase(code, faultStatusEnum.getCode())) {
                return faultStatusEnum;
            }
        }
        return null;
    }
}
