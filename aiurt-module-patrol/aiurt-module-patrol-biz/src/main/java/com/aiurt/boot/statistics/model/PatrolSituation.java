package com.aiurt.boot.statistics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @author JB
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PatrolSituation implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 巡视总数
     */
    @ApiModelProperty(value = "巡视总数")
    private Long sum;
    /**
     * 已巡视数
     */
    @ApiModelProperty(value = "已巡视数")
    private Long finish;
    /**
     * 未巡视数
     */
    @ApiModelProperty(value = "未巡视数")
    private Long unfinish;
    /**
     * 检修中数
     */
    @ApiModelProperty(value = "巡视中数")
    private Long overhaul;
    /**
     * 异常数
     */
    @ApiModelProperty(value = "异常数")
    private Long abnormal;
    /**
     * 漏巡视总数
     */
    @ApiModelProperty(value = "漏巡视总数")
    private Long omit;
    /**
     * 漏巡视率
     */
    @ApiModelProperty(value = "漏巡视率")
    private String omitRate;

    /**
     * 巡检的日期(yyyy-MM-dd)
     */
    @Excel(name = "巡检的日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "巡检的日期(yyyy-MM-dd)")
    private java.util.Date patrolDate;
    /**
     * 手工下发巡检的开始日期(yyyy-MM-dd)
     */
    @Excel(name = "手工下发巡检的开始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "手工下发巡检的开始日期(yyyy-MM-dd)")
    private java.util.Date startDate;
    /**
     * 手工下发巡检的结束日期(yyyy-MM-dd)
     */
    @Excel(name = "手工下发巡检的结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "手工下发巡检的结束日期(yyyy-MM-dd)")
    private java.util.Date endDate;
}
