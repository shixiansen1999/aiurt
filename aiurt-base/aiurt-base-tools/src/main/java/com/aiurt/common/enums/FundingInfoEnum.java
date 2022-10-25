package com.aiurt.common.enums;

/**
 * @author: zwl
 */
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
