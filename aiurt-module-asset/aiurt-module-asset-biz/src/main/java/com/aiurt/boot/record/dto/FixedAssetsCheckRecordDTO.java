package com.aiurt.boot.record.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 盘点记录结果分页查询DTO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetsCheckRecordDTO {
    /**
     * 盘点任务表主键
     */
    @ApiModelProperty(value = "盘点任务表主键")
    private String checkId;
    /**
     * 资产名称
     */
    @ApiModelProperty(value = "资产名称")
    private String assetName;
    /**
     * 盘点结果
     */
    @ApiModelProperty(value = "盘点结果")
    private Integer result;
    /**
     * 资产分类编码
     */
    @ApiModelProperty(value = "资产分类编码")
    private String categoryCode;
    /**
     * 资产编号
     */
    @ApiModelProperty(value = "资产编号")
    private String assetCode;
}
