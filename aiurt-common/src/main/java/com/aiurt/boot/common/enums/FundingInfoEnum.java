package com.aiurt.boot.common.enums;

public enum FundingInfoEnum {
    ZIZHUTONGZHI(1),
    YEWUBAIKE(2),
    ZIZHUZHENGCE(3),
    YURENHUODONG(4);
    private Integer code;

    FundingInfoEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
}
