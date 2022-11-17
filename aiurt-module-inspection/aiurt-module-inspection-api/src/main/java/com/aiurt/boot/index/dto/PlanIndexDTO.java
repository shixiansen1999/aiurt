package com.aiurt.boot.index.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PlanIndexDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 班组名称
     */
    @ApiModelProperty(value = "班组名称")
    private String  teamName;
    /**
     * 检修总数
     */
    @ApiModelProperty(value = "检修总数")
    private Long sum;
    /**
     * 已检修数
     */
    @ApiModelProperty(value = "已检修数")
    private Long finish;
    /**
     * 检修中数
     */
    @ApiModelProperty(value = "检修中数")
    private Long overhaul;

    /**
     * 今日已检修数
     */
    @ApiModelProperty(value = "今日已检修数")
    private Long todayFinish;
    /**
     * 未检修数
     */
    @ApiModelProperty(value = "未检修数")
    private Long unfinish;
    /**
     * 未检修数率
     */
    @ApiModelProperty(value = "未检修数率")
    private String unfinishRate;
    /**
     * 已检修率
     */
    @ApiModelProperty(value = "已检修率")
    private String finishRate;
    /**
     * 漏检修总数
     */
    @ApiModelProperty(value = "漏检修总数")
    private Long omit;
    /**
     * 漏检修率
     */
    @ApiModelProperty(value = "漏检修率")
    private String omitRate;
    /**
     * 开始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private Date startDate;
    /**
     * 结束日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;
}
