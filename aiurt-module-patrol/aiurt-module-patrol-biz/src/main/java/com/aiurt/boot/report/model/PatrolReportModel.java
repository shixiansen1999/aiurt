package com.aiurt.boot.report.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
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
    private String lineCode;

    /**
     * 站点code
     */
    private String stationCode;
    /**
     * 班组code
     */
    private String orgCode;
    /**
     * 子系统code
     */
    private String subsystemCode;
    /**
     * 开始日期
     */
    private Date startDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 结束时间
     */
    private List<String> orgList;
    @ApiModelProperty("分页参数")
    private Integer pageNo;
    @ApiModelProperty("分页参数")
    private Integer pageSize;
}
