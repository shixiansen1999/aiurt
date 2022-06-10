package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
/**
 * @Author km
 * @Date 2021/9/22 19:46
 * @Version 1.0
 */
@Data
public class SparePartInExcel {

    /**主键id*/
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private  Long  id;

    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    /**所属系统*/
    @Excel(name = "所属系统", width = 15)
    @ApiModelProperty(value = "所属系统")
    private String system;

    /**物资编号*/
    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private  String  materialCode;

    /**所在班组*/
    @Excel(name = "所在班组", width = 15)
    @ApiModelProperty(value = "所在班组")
    private  String  orgId;

    @ApiModelProperty(value = "备件类型")
    private  Integer  type;

    /**备件类型名称*/
    @Excel(name = "备件类型", width = 15)
    @ApiModelProperty(value = "备件类型")
    private  String  typeName;

    /**规格型号*/
    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String specifications;

    /**生产厂家*/
    @Excel(name = "生产厂家", width = 15)
    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    /**单位*/
    @Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    private String unit;

    /**入库数量*/
    @Excel(name = "入库数量", width = 15)
    @ApiModelProperty(value = "入库数量")
    private  Integer  num;

    /**存放位置*/
    @Excel(name = "存放位置", width = 15)
    @ApiModelProperty(value = "存放位置")
    private  String  warehouseName;

    /**入库时间*/
    @Excel(name = "入库时间", width = 20)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "入库时间")
    private  java.util.Date  joinTime;

    /**品牌*/
    @Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
    private String brand;

    /**确认状态(0.未确认 1.已确认)*/
    @Excel(name = "确认状态(0.未确认 1.已确认)", width = 15)
    @ApiModelProperty(value = "确认状态(0.未确认 1.已确认)")
    private  Integer  confirmStatus;

    /**确认状态*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "确认状态")
    private  String  confirmStatusDesc;




}
