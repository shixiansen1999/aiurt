package com.aiurt.boot.report.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/9/19
 * @desc
 */
@Data
public class PatrolReportModel {
    /**
     * 线路code
     */
    @ApiModelProperty("线路code")
    private String lineCode;

    /**
     * 站点code
     */
    @ApiModelProperty("站点code")
    private String stationCode;
    /**
     * 站点List
     */
    @ApiModelProperty("站点List")
    private List<String> stationCodeList;
    /**
     * 班组code
     */
    @ApiModelProperty("班组code")
    private String orgCode;
    /**
     * 子系统code
     */
    @ApiModelProperty("子系统code")
    private String subsystemCode;
    /**
     * 开始日期
     */
    @ApiModelProperty("开始日期")
    private String startDate;
    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private String endDate;
    private List<String> orgCodeList;
    private List<String> orgIdList;
    @ApiModelProperty("分页参数")
    private Integer pageNo;
    @ApiModelProperty("分页参数")
    private Integer pageSize;
}
