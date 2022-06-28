package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/6/28
 * @desc
 */
@Data
public class PatrolAccompanyDTO {
    /**巡检单号*/
    @Excel(name = "巡检单号", width = 15)
    @ApiModelProperty(value = "巡检单号")
    private java.lang.String taskDeviceCode;
    /**同行人ID*/
    @Excel(name = "同行人ID", width = 15)
    @ApiModelProperty(value = "同行人ID")
    private java.lang.String userId;
    /**同行人名称*/
    @Excel(name = "同行人名称", width = 15)
    @ApiModelProperty(value = "同行人名称")
    private java.lang.String username;
}
