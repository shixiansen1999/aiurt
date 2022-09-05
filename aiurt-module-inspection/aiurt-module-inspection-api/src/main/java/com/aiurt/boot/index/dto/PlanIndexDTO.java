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
     * 未检修数
     */
    @ApiModelProperty(value = "未检修数")
    private Long unfinish;
    /**
     * 已检修率
     */
    @ApiModelProperty(value = "已检修率")
    private Double finishRate;
    /**
     * 漏检修总数
     */
    @ApiModelProperty(value = "漏检修总数")
    private Long omit;
    /**
     * 漏检修率
     */
    @ApiModelProperty(value = "漏检修率")
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
