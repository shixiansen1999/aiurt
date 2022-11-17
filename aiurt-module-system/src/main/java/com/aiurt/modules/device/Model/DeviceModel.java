package com.aiurt.modules.device.Model;

import com.aiurt.common.aspect.annotation.*;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Data
public class DeviceModel implements Serializable {


    /**主键id*/
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private  String  id;

    /**所属专业*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "所属专业")
    @MajorFilterColumn
    private  String  majorCode;


    /**子系统*/
    @Excel(name = "子系统", width = 15)
    @ApiModelProperty(value = "子系统")
    @SystemFilterColumn
    private  String  systemCode;


    /**设备名称*/
    @Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private  String  name;

    /**设备编号*/
    @Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    private  String  code;

    /**设备类型*/
    @Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    private  String  deviceTypeCode;


    /**设备图纸*/
    @Excel(name = "设备图纸", width = 15,type = 2,savePath = "jeecg.path.upload")
    @ApiModelProperty(value = "设备图纸")
    private  String  picturePath;

    /**线路编号*/
    @Excel(name = "线路", width = 15)
    @ApiModelProperty(value = "线路编号")
    @LineFilterColumn
    private  String  lineCode;


    /**站点编号*/
    @Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点编号")
    @StaionFilterColumn
    private  String  stationCode;


    /**存放位置*/
    @Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
    private  String  positionCode;

    /**资产编号*/
    @Excel(name = "资产编号", width = 15)
    @ApiModelProperty(value = "资产编号")
    private  String  assetCode;

    /**设备管理员*/
    @Excel(name = "设备管理员", width = 15)
    @ApiModelProperty(value = "设备管理员")
    private  String  manageUserName;

    /**负责班组*/
    @Excel(name = "负责班组", width = 15)
    @ApiModelProperty(value = "负责班组")
    @DeptFilterColumn
    private  String  orgCode;

    /**
     * 临时设备(是/否（默认否）1是,0:否)
     */
    @Excel(name = "临时设备", width = 15)
    @ApiModelProperty(value = "临时设备(是/否（默认否）1是,0:否)")
    private  String  temporary;

    /**设备复用类型(1:多线路复用/0:多站点复用)*/
	@Excel(name = "设备复用类型", width = 15)
    @ApiModelProperty(value = "设备复用类型(1:多线路复用/0:多站点复用)")
    private  String  reuseType;


    /**出厂日期*/
    @Excel(name = "出厂日期", width = 15)
    @ApiModelProperty(value = "出厂日期")
    private  String  factoryDate;

    /**设备等级(字典值)*/
    @ApiModelProperty(value = "设备等级(字典值)")
    private  String  deviceLevel;


    /**品牌*/
    @Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
    private  String  brand;

    /**供应商(厂商信息表_编码)*/
    @Excel(name = "供应商(厂商信息表_编码)", width = 15)
    @ApiModelProperty(value = "供应商(厂商信息表_编码)")
    private  String  manufactorCode;

    /**设备ip*/
    @Excel(name = "设备IP", width = 15)
    @ApiModelProperty(value = "设备ip")
    private  String  deviceIp;

    /**出厂编号*/
    @Excel(name = "出厂编号", width = 15)
    @ApiModelProperty(value = "出厂编号")
    private  String  factoryCode;

    /**生产日期*/
    @Excel(name = "生产日期", width = 15)
    @ApiModelProperty(value = "生产日期")
    private  String  productionDate;

    /**开始使用日期*/
    @Excel(name = "开始使用日期", width = 15)
    @ApiModelProperty(value = "开始使用日期")
    private  String  startDate;

    /**使用年限*/
    @Excel(name = "使用年限", width = 15)
    @ApiModelProperty(value = "使用年限")
    private  String  serviceLife;

    /**技术参数*/
    @Excel(name = "技术参数", width = 15)
    @ApiModelProperty(value = "技术参数")
    private  String  technicalParameter;

    /**
     * 状态
     */
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态 0-停用 1-正常")
    private  String  status;

    /**价格*/
    @Excel(name = "价格", width = 15)
    @ApiModelProperty(value = "价格")
    private  String  price;

    /**设备sn*/
    @Excel(name = "设备SN", width = 15)
    @ApiModelProperty(value = "设备sn")
    @TableField(value = "device_sn")
    private  String  deviceSn;

    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  mistake;
}
