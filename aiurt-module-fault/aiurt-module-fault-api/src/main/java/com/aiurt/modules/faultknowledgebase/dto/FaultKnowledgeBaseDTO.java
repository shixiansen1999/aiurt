package com.aiurt.modules.faultknowledgebase.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zwl
 */
@Data
@ApiModel("导出数据")
public class FaultKnowledgeBaseDTO {

    /**知识库类别编码*/
    @ApiModelProperty(value = "知识库类别编码")
    @TableField(exist = false)
    private String knowledgeBaseTypeCode;
    /**知识库类别名称*/
    @Excel(name = "故障现象分类", width = 15)
    @ApiModelProperty(value = "故障现象分类")
    @TableField(exist = false)
    private String knowledgeBaseTypeName;
    /**设备类型编码*/
    @ApiModelProperty(value = "设备类型编码")
    @TableField(exist = false)
    private String deviceTypeCode;
    /**设备类型名称*/
    @Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    @TableField(exist = false)
    private String deviceTypeName;
    /**设备组件编码*/
    @ApiModelProperty(value = "设备组件编码")
    @TableField(exist = false)
    private String materialCode;
    /**设备组件名称*/
    @Excel(name = "设备组件", width = 15)
    @ApiModelProperty(value = "设备组件")
    @TableField(exist = false)
    private String materialName;
    /**故障现象*/
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    @TableField(exist = false)
    private String faultPhenomenon;
    /**故障原因*/
    @Excel(name = "故障原因", width = 15)
    @ApiModelProperty(value = "故障原因")
    @TableField(exist = false)
    private String faultReason;
    /**解决方案*/
    @Excel(name = "解决方案", width = 15)
    @ApiModelProperty(value = "解决方案")
    @TableField(exist = false)
    private String solution;
    /**排查方法*/
    @Excel(name = "排查方法", width = 15)
    @ApiModelProperty(value = "排查方法")
    @TableField(exist = false)
    private String method;
    /**携带工具*/
    @Excel(name = "携带工具", width = 15)
    @ApiModelProperty(value = "携带工具")
    @TableField(exist = false)
    private String tools;
}
