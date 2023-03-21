package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LKJ
 */
@Data
public class PrintSystemDTO {
    @ApiModelProperty(value = "系统名称")
    private String systemName;
    @ApiModelProperty(value = "巡视内容")
    private List<PrintDetailDTO> printDetailDTOS;
}
