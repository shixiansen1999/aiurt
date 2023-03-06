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
    /**
     * 执行巡视任务
     */
    PATROL_EXECUTE("patrol_execute"),
    /**
     * 审核巡视任务
     */
    PATROL_AUDIT("patrol_audit"),
    /**
     * 巡视任务漏巡
     */
    PATROL_OMIT("patrol_omit"),

    /**
     * 故障审核
     */
    FAULT_APPROVAL("fault_approval"),
    /**
     * 故障指派
     */
    FAULT_ASSIGN("fault_assign"),
    /**
     * 故障处理
     */
    FAULT_DEAL("fault_deal"),

    /**
     * 故障挂起
     */
    FAULT_HANG_UP("fault_hang_up"),
    FAULT_RESULT("fault_result"),
    /**
     * 物资出入库
     */
    MATERIAL_WAREHOUSING("material_warehousing"),


    /**
     * 固定资产盘点
     */
    FIXED_ASSETS("fixed_assets");

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
