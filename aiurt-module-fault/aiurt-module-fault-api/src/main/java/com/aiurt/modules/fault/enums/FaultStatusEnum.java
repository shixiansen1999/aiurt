package com.aiurt.modules.fault.enums;

import cn.hutool.core.util.StrUtil;

public enum  FaultStatusEnum {

    NEW_FAULT("1", "故障上报", 1),
    ASSEMBLY_SOURCE_CODE("2", "故障上报", 1);




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
