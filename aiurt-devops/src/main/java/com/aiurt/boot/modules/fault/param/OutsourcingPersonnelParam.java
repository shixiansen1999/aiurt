package com.aiurt.boot.modules.fault.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

/**
 * @Author: swsc
 * 委外人员查询参数列表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class OutsourcingPersonnelParam {
    /**
     * 姓名
     */
    private String name;

    /**
     * 所属单位
     */
    private String company;

    /**
     * 职位名称
     */
    private String position;

    /**
     * 所属专业系统编号
     */
    private String systemCode;

    /**
     * 联系方式
     */
    private String connectionWay;

}
