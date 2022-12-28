package com.aiurt.common.constant.enums;

import com.aiurt.common.util.oConvertUtils;

/**
 * 待办任务业务类型枚举类
 * @author: aiurt
 */
public enum TodoBusinessTypeEnum {


    /**
     * 执行检修任务
     */
    INSPECTION_EXECUTE("inspection_execute"),
    /**
     * 审核检修任务
     */
    INSPECTION_CONFIRM("inspection_confirm"),
    /**
     * 验收检修任务
     */
    INSPECTION_RECEIPT("inspection_receipt"),
    ;



    /**
     * 业务类型(email:邮件 bpm:流程)
     */
    private String type;


    TodoBusinessTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public static TodoBusinessTypeEnum getByType(String type) {
        if (oConvertUtils.isEmpty(type)) {
            return null;
        }
        for (TodoBusinessTypeEnum val : values()) {
            if (val.getType().equals(type)) {
                return val;
            }
        }
        return null;
    }
}
