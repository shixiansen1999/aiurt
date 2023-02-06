package com.aiurt.boot.rehearsal.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

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
     * 审核状态（1待提交、2审核中、3已通过）
     */
    @ApiModelProperty(value = "审核状态（1待提交、2审核中、3已通过）")
    private Integer status;
    /**
     * 组织机构编码
     */
    @ApiModelProperty(value = "组织机构编码")
    private String orgCode;
    /**
     * 所属年份
     */
    @ApiModelProperty(value = "所属年份")
    private String year;
    /**
     * 接口标识，0年演练计划列表查询，1月演练计划列表查询，2演练计划审核列表查询
     */
    @ApiModelProperty(value = "接口标识，0年演练计划列表查询，1月演练计划列表查询，2演练计划审核列表查询")
    private Integer flag;
    /**
     * 组织机构编码集合，本级及其所属的子级
     */
    private List<String> orgCodes;
}
