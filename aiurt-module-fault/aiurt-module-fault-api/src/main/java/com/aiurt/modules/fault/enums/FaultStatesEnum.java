package com.aiurt.modules.fault.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author fgw
 */
public enum FaultStatesEnum {
    /**
     * 故障上报,待审批
     */
    DOING("1", "未完成", 1),

    /**
     * 已完成
     */
    FINISH("2", "已完成", 2),

    /**
     * 已挂起
     */
    HANGUP("3", "已挂起",3),
    /**
     * 已取消
     */
    CANCEL("0", "已取消",0);

    private String code;
    private String message;
    private Integer status;

    private FaultStatesEnum(String code, String message, Integer status){
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


    public static FaultStatesEnum getByCode(String code) {
        for (FaultStatesEnum faultStatesEnum : FaultStatesEnum.values()) {
            if (StrUtil.equalsIgnoreCase(code, faultStatesEnum.getCode())) {
                return faultStatesEnum;
            }
        }
        return null;
    }
}
