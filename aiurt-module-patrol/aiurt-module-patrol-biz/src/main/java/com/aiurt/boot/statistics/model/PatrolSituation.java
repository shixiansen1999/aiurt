package com.aiurt.boot.statistics.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
}
