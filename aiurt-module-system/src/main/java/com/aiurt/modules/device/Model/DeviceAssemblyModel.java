package com.aiurt.modules.device.Model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class DeviceAssemblyModel {

    /**主键id*/
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private  Long  id;

    /**组件类型/物质类型*/
    @ApiModelProperty(value = "物资类型编码")
    @Dict(dictTable ="material_base_type",dicText = "base_type_name",dicCode = "base_type_code")
    private  String  baseTypeCode;

    /**组件类型/物质类型*/
    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "物资类型")
    @TableField(exist = false)
    private  String  baseTypeCodeName;

    /**组件状态*/
    @ApiModelProperty(value = "组件状态")
    @Dict(dicCode = "device_assembly_status")
    private  String  assemblyStatus;

    /**组件状态*/
    @Excel(name = "组件状态", width = 15)
    @ApiModelProperty(value = "组件状态")
    @TableField(exist = false)
    private  String  statusName;

    /**组件编号*/
    @Excel(name = "组件编号", width = 15)
    @ApiModelProperty(value = "组件编号")
    private  String  assemblyCode;

    /**生产厂商（厂商信息）*/
    @Excel(name = "生产厂商（厂商信息）", width = 15)
    @ApiModelProperty(value = "生产厂商（厂商信息）")
    private  String  manufactorCode;

    /**设备编号*/
    @Excel(name = "所属设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    private  String  deviceCode;

    /**组件名称*/
    @Excel(name = "组件名称", width = 15)
    @ApiModelProperty(value = "组件名称")
    private  String  materialName;

    /**物资基础数据编号-物资编号*/
    @Excel(name = "物资基础数据编号-物资编号", width = 15)
    @ApiModelProperty(value = "物资基础数据编号-物资编号")
    private  String  materialCode;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  remark;

    /**规格*/
    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private  String  specifications;


    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @TableField(exist = false)
    private java.util.Date startDate;

    /**附件*/
    @Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private  String  path;

    /**价格（元）*/
    @Excel(name = "价格（元）", width = 15)
    @ApiModelProperty(value = "价格（元）")
    private  String  price;

    /**设备类型编码*/
    @Excel(name = "设备类型编码", width = 15)
    @ApiModelProperty(value = "设备类型编码")
    private  String  deviceTypeCode;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "购买日期")
    private java.util.Date buyDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "上线")
    private java.util.Date onlineDate;

    /**单位*/
    @Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    private  String  unit;

    /**
     * 是否是易耗品：0否1是
     */
    @Excel(name = "是否是易耗品：0否 1是", width = 15)
    @ApiModelProperty(value = "是否是易耗品：0否 1是 默认为0")
    @Dict(dicCode = "consumables_type")
    private java.lang.Integer consumablesType = 0;

    @Excel(name = "是否是易耗品：0否 1是", width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "是否是易耗品名称")
    private java.lang.String consumablesName;

    /**错误原因*/
    @Excel(name = "错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  mistake;
}
