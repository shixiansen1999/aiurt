package com.aiurt.boot.rehearsal.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author
 * @date 2022/11/29 10:54
 * @description:
 */
@Data
public class EmergencyRehearsalYearDTO {
    /**
     * 计划编号
     */
    @ApiModelProperty(value = "计划编号")
    private String code;
    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称")
    private String name;
    /**
     * 审核状态（1待提交、2待审核、3审核中、4已通过）
     */
    @ApiModelProperty(value = "审核状态（1待提交、2待审核、3审核中、4已通过）")
    private Integer status;
    /**
     *组织机构编码
     */
    @ApiModelProperty(value = "组织机构编码")
    private String orgCode;

}
