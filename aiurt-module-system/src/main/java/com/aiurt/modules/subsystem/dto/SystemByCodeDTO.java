package com.aiurt.modules.subsystem.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.modules.subsystem.dto
 * @className: SystemByCodeDTO
 * @author: life-0
 * @date: 2022/10/20 10:59
 * @description: TODO
 * @version: 1.0
 */
@Data
public class SystemByCodeDTO {
    @ApiModelProperty(value = "子系统名称")
    private String systemName;
    @ApiModelProperty(value = "子系统Code")
    private String systemCode;
    @ApiModelProperty(value = "系统概括")
    private String description;
    @ApiModelProperty(value = "维修次数")
    private Integer repairNum;
    @ApiModelProperty(value = "上次维修时间")
    private String repairTime;
    @ApiModelProperty(value = "更换部件次数")
    private Integer replacementNum;
}
