package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: PrintDTO
 * @author: hqy
 * @date: 2023/5/26 14:30
 * @version: 1.0
 */
@Data
public class PrintDTO {
    @ApiModelProperty(value = "主键ID")
    private String id;
    @ApiModelProperty(value = "地点")
    private String location;
    @ApiModelProperty(value = "子系统")
    private String subSystem;
    @ApiModelProperty(value = "设备")
    private String equipment;
    @ApiModelProperty(value = "巡检标准")
    private String standard;
    @ApiModelProperty(value = "巡检结果")
    private String result;
    @ApiModelProperty(value = "备注")
    private String remark;
}
