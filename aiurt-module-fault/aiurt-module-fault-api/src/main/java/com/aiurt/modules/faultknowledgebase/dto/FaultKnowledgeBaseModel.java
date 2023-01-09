package com.aiurt.modules.faultknowledgebase.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
/**
 * @author zwl
 * @version 1.0
 * @date 2022/1/5
 * @desc
 */
public class FaultKnowledgeBaseModel {

    /**主键id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;

    /**知识库编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "知识库编码")
    private java.lang.String knowledgeBaseTypeCode;

    /**知识库名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "知识库名称")
    @Excel(name = "知识库类别", width = 15)
    private java.lang.String knowledgeBaseTypeName;

    /**设备类型编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "设备类型编码")
    private java.lang.String deviceTypeCode;

    /**设备类型名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "设备类型名称")
    @Excel(name = "设备类型", width = 15)
    private java.lang.String deviceTypeName;

    /**设备组件编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "设备组件编码")
    private java.lang.String materialCode;

    /**设备组件名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "设备组件名称")
    @Excel(name = "设备组件", width = 15)
    private java.lang.String materialName;

    /**设备组件名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "故障现象")
    @Excel(name = "故障现象", width = 15)
    private java.lang.String faultPhenomenon;


    /**设备组件名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "故障原因")
    @Excel(name = "故障原因", width = 15)
    private java.lang.String faultReason;


    /**解决方案*/
    @TableField(exist = false)
    @ApiModelProperty(value = "解决方案")
    @Excel(name = "解决方案", width = 15)
    private java.lang.String solution;

    /**排查方法*/
    @TableField(exist = false)
    @ApiModelProperty(value = "排查方法")
    @Excel(name = "排查方法", width = 15)
    private java.lang.String method;

    /**携带工具*/
    @TableField(exist = false)
    @ApiModelProperty(value = "携带工具")
    @Excel(name = "携带工具", width = 15)
    private java.lang.String tools;

    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  deviceMistake;


}
