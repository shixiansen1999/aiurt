package com.aiurt.modules.material.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class MaterialBaseTypeDTO {


    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private  String  majorName;
    @Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    private  String  systemName;

    /**分类编码*/
    @Excel(name = "分类编码", width = 15)
    @ApiModelProperty(value = "分类编码")
    private  String  baseTypeCode;

    /**分类名称*/
    @Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
    private  String  baseTypeName;

    /**设备类型编码*/
    @Excel(name = "分类状态", width = 15)
    @ApiModelProperty(value = "分类状态,默认启用，1：启用，0停用")
    @Dict(dicCode ="material_base_type_status")
    private  String  status;
}
