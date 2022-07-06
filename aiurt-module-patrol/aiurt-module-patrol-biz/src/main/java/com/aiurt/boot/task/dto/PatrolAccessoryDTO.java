package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/1
 * @desc
 */
@Data
public class PatrolAccessoryDTO {
    /**附件名称*/
    @Excel(name = "附件名称", width = 15)
    @ApiModelProperty(value = "附件名称")
    private java.lang.String name;
    /**附件地址*/
    @Excel(name = "附件地址", width = 15)
    @ApiModelProperty(value = "附件地址")
    private java.lang.String address;
}
