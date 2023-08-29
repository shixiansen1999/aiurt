package com.aiurt.modules.train.task.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Administrator
 * 2022/4/22
 * 报表管理请求参数实体
 */
@Data
public class ReportReqVO {
    @ApiModelProperty("年份")
    private String year;
    @ApiModelProperty("季度")
    private Integer season;
    @ApiModelProperty("月份")
    private Integer month;
    @ApiModelProperty("部门")
    private String sysOrgCode;
    @ApiModelProperty("分页参数")
    private Integer pageNo;
    @ApiModelProperty("分页参数")
    private Integer pageSize;
    @ApiModelProperty("培训开始时间")
    private String trainStart;
    @ApiModelProperty("培训结束时间")
    private String trainEnd;

}
