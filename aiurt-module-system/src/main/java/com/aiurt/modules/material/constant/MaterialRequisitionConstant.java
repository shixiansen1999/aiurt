package com.aiurt.modules.material.constant;

/**
 * 领料单常量类
 *
 * @author 华宜威
 * @date 2023-09-21 11:40:43
 */
public interface MaterialRequisitionConstant {

    /**申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成*/
    Integer STATUS_TO_BE_SUBMITTED = 1;
    Integer STATUS_TO_BE_CONFIRMED = 2;
    Integer STATUS_CONFIRMED = 3;
    Integer STATUS_REVIEWING = 4;
    Integer STATUS_PASSED = 5;
    Integer STATUS_REJECTED = 6;
    Integer STATUS_COMPLETED = 7;

    /**提交状态（0-未提交 1-已提交）*/
    Integer COMMIT_STATUS_NOT_SUBMITTED = 0;
    Integer COMMIT_STATUS_SUBMITTED = 1;

    /**申领单类型（1维修领用，3三级库领用，2二级库领用）*/
    Integer MATERIAL_REQUISITION_TYPE_REPAIR = 1;
    Integer MATERIAL_REQUISITION_TYPE_LEVEL2 = 2;
    Integer MATERIAL_REQUISITION_TYPE_LEVEL3 = 3;

    /**领用类型（1特殊领用，2普通领用）*/
    Integer APPLY_TYPE_SPECIAL = 1;
    Integer APPLY_TYPE_NORMAL = 2;
}
