package com.aiurt.boot.record.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 盘点检查结果统计VO对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "盘点检查结果统计VO对象", description = "盘点检查结果统计VO对象")
public class CheckResultTotalVO {

    /**
     * 盘盈
     */
    @ApiModelProperty(value = "盘盈")
    private Long profit;
    /**
     * 盘亏
     */
    @ApiModelProperty(value = "盘亏")
    private Long loss;
    /**
     * 盘平
     */
    @ApiModelProperty(value = "盘平")
    private Long equality;
}
