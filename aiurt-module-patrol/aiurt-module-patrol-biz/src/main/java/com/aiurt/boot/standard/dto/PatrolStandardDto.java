package com.aiurt.boot.standard.dto;

import com.aiurt.boot.standard.entity.PatrolStandard;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.standard.dto
 * @className: PatrolStandardDto
 * @author: life-0
 * @date: 2022/6/23 17:33
 * @description: TODO
 * @version: 1.0
 */
@Data
public class PatrolStandardDto extends PatrolStandard {
    @Excel(name = "适用系统名称", width = 15)
    @ApiModelProperty(value = "适用系统名称")
    @TableField(exist = false)
    private java.lang.String subsystemName;
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private java.lang.String professionName;
    @Excel(name = "设备类型名称", width = 15)
    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private java.lang.String deviceTypeName;
}
