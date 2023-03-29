package com.aiurt.modules.train.task.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Administrator
 * 2022/4/22
 */
@Data
public class ReCheckReqVo implements Serializable {
    @ApiModelProperty(value = "复核状态")
    private Integer reCheckState;
    @ApiModelProperty(value = "培训部门")
    private String sysOrgCode;
    @ApiModelProperty(value = "培训部门id")
    private String sysOrgCodeId;
    @ApiModelProperty(value = "参考人员")
    private String examPerson;
    @ApiModelProperty(value = "考试计划")
    private String examPlan;
    @ApiModelProperty(value = "是否及格")
    private Integer isPass;
    @ApiModelProperty("分页参数")
    private Integer pageNo;
    @ApiModelProperty("分页参数")
    private Integer pageSize;
}
