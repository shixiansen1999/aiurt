package com.aiurt.modules.device.Model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceAssemblyErrorModel {

    /**所属专业*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "所属专业")
    private  String  majorCodeName;


    /**子系统*/
    @Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    private  String  systemCodeName;

    /**设备类型*/
    @Excel(name = "设备类型名称", width = 15)
    @ApiModelProperty(value = "设备类型名称")
    private  String  deviceTypeCodeName;

    /**设备编号*/
    @Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    private  String  code;

    /**设备名称*/
    @Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private  String  name;

    /**状态*/
    @Excel(name = "设备状态", width = 15)
    @ApiModelProperty(value = "状态 0-停用 1-正常")
    private  String  status;

    /**线路编号*/
    @Excel(name = "线路", width = 15)
    @ApiModelProperty(value = "线路名称")
    private  String  lineCodeName;


    /**站点编号*/
    @Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点名称")
    private  String  stationCodeName;


    /**存放位置*/
    @Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置名称")
    private  String  positionCodeName;

    /**负责班组*/
    @Excel(name = "负责班组", width = 15)
    @ApiModelProperty(value = "负责班组")
    private  String  orgCodeName;

    /**设备管理员*/
    @Excel(name = "设备管理员", width = 15)
    @ApiModelProperty(value = "设备管理员")
    private  String  manageUserName;

    /**设备等级(字典值)*/
    @Excel(name = "设备等级", width = 15)
    @ApiModelProperty(value = "设备等级(字典值)")
    private  String  deviceLevel;


    /**临时设备(是/否（默认否）1是,0:否)*/
    @Excel(name = "临时设备", width = 15)
    @ApiModelProperty(value = "临时设备(是/否（默认否）1是,0:否)")
    private  String  temporary;

    /**组件类型/物质类型*/
    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "物资类型")
    @TableField(exist = false)
    private  String  baseTypeCodeName;

    /**组件状态*/
    @Excel(name = "组件状态", width = 15)
    @ApiModelProperty(value = "组件状态")
    @TableField(exist = false)
    private  String  assemblyStatus;

    /**组件编号*/
    @Excel(name = "组件编号", width = 15)
    @ApiModelProperty(value = "组件编号")
    private  String  assemblyCode;

    /**组件名称*/
    @Excel(name = "组件名称", width = 15)
    @ApiModelProperty(value = "组件名称")
    private  String  materialName;

    /**物资基础数据编号-物资编号*/
    @Excel(name = "物资基础数据编号-物资编号", width = 15)
    @ApiModelProperty(value = "物资基础数据编号-物资编号")
    private  String  materialCode;

    /**错误原因*/
    @Excel(name = "组件数据错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  mistake;

    /**错误原因*/
    @Excel(name = "设备主数据错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  deviceMistake;
}
