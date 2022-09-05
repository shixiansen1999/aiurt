package com.aiurt.boot.statistics.dto;

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
     * 漏巡视总数
     */
    @ApiModelProperty(value = "漏巡视总数")
    private Long omit;
    /**
     * 漏巡视率
     */
    @ApiModelProperty(value = "漏巡视率")
    private Double omitRate;
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
