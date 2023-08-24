package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: PrintDataDTO
 * @author: hqy
 * @date: 2023/8/8 14:29
 * @version: 1.0
 */
@Data
public class PrintDataDTO {
    String data;
    @ApiModelProperty(value = "巡检结果正常")
    private String resultTrue;
    @ApiModelProperty(value = "巡检结果异常")
    private String resultFalse;
    @ApiModelProperty(value = "备注")
    private String remark;
    private String contentRemark;
    @ApiModelProperty(value = "巡检结果异常和无")
    private String result;
    @ApiModelProperty(value = "巡检结果和备注")
    private String resultAndRemark;
}
