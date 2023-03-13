package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LKJ
 */
@Data
public class PrintDetailDTO {

    @ApiModelProperty(value = "子系统")
    private String systemName;

    @ApiModelProperty(value = "巡视内容及标准")
    private String content;

    @ApiModelProperty(value = "巡视结果")
    private String result;

    @ApiModelProperty(value = "备注")
    private String remark;
}
