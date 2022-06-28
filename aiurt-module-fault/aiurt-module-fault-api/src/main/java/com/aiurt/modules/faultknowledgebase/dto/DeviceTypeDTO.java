package com.aiurt.modules.faultknowledgebase.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
@Data
@ApiModel("设备分类")
public class DeviceTypeDTO {

    /**主键id*/
    @ApiModelProperty(value = "主键id")
    private String id;
    /**所属专业*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "所属专业")
    private String majorCode;
    /**系统编号*/
    @Excel(name = "系统编号", width = 15)
    @ApiModelProperty(value = "系统编号")
    private String systemCode;
    /**分类编号*/
    @Excel(name = "分类编号", width = 15)
    @ApiModelProperty(value = "分类编号")
    private String code;
    /**分类名称*/
    @Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
    private String name;
    /**状态 0-停用 1-正常*/
    @Excel(name = "状态 0-停用 1-正常", width = 15)
    @ApiModelProperty(value = "状态 0-停用 1-正常")
    private Integer status;
}
