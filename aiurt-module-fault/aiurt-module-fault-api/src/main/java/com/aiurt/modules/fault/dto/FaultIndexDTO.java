package com.aiurt.modules.fault.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 故障概况
 *
 * @author: qkx
 * @date: 2022-09-06 12:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultIndexDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 故障总数
     */
    @ApiModelProperty(value = "故障总数")
    private Long sum;
    /**
     * 已解决数
     */
    @ApiModelProperty(value = "已解决数")
    private Long solve;
    /**
     * 未解决数
     */
    @ApiModelProperty(value = "未解决数")
    private Long unSolve;
    /**
     * 挂起数
     */
    @ApiModelProperty(value = "挂起数")
    private Long hang;
    /**
     * 已检修率
     */
    @ApiModelProperty(value = "已解决率")
    private String solveRate;
    /**
     * 一级数量
     */
    @ApiModelProperty(value = "一级数量")
    private int levelOneNumber;
    /**
     * 二级数量
     */
    @ApiModelProperty(value = "二级数量")
    private int levelTwoNumber;
    /**
     * 三级数量
     */
    @ApiModelProperty(value = "三级数量")
    private int levelThreeNumber;
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
