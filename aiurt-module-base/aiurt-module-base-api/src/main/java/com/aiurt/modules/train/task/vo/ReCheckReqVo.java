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
    @ApiModelProperty(name = "复核状态")
    private Integer reCheckState;
    @ApiModelProperty(name = "培训部门")
    private String sysOrgCode;
    @ApiModelProperty(name = "培训部门")
    private String sysOrgCodeId;
    @ApiModelProperty(name = "参考人员")
    private String examPerson;
    @ApiModelProperty(name = "考试计划")
    private String examPlan;
    @ApiModelProperty(name = "是否及格")
    private Integer isPass;
    @ApiModelProperty("分页参数")
    private Integer pageNo;
    @ApiModelProperty("分页参数")
    private Integer pageSize;
}
