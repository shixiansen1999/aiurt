package com.aiurt.boot.screen.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
/**
 * @author JB
 * @Description: 大屏巡视模块-重要数据展示对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "ScreenImportantData", description = "大屏巡视模块-重要数据展示对象")
public class ScreenImportantData {
    /**
     * 计划巡视数
     */
    @ApiModelProperty(value = "计划巡视数")
    private java.lang.Long patrolNumber;
    /**
     * 计划巡视数
     */
    @ApiModelProperty(value = "巡视完成数")
    private java.lang.Long finishNumber;
    /**
     * 计划巡视数
     */
    @ApiModelProperty(value = "漏检数")
    private java.lang.Long omitNumber;
}
