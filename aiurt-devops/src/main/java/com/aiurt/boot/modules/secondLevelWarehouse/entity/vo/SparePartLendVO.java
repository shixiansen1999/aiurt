package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author km
 * @Date 2021/9/22 13:37
 * @Version 1.0
 */
@Data
public class SparePartLendVO{

    @ApiModelProperty(value = "主键id")
    private  Long  id;

    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    @Excel(name = "所属系统", width = 15)
    @ApiModelProperty(value = "所属系统")
    private String system;

    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private  String  materialCode;

    @Excel(name = "备件名称", width = 15)
    @ApiModelProperty(value = "备件名称")
    private String materialName;

    @ApiModelProperty(value = "备件类型（1：非生产类型 2：生产类型）")
    private Integer type;

    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "物资类型名称（1：非生产类型 2：生产类型）")
    private String typeName;

    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String specifications;

    @Excel(name = "生产厂家", width = 15)
    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @Excel(name = "借入数量", width = 15)
    @ApiModelProperty(value = "借入数量")
    private  Integer  lendNum;

    @ApiModelProperty(value = "借出确认(0-未确认 1-已确认)")
    private  Integer  lendConfirm;

    @Excel(name = "申请时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请时间")
    private  java.util.Date  lendTime;

    @Excel(name = "借入部门", width = 15)
    @ApiModelProperty(value = "借入部门")
    private  String  lendDepart;

    @Excel(name = "借用人", width = 15)
    @ApiModelProperty(value = "借用人")
    private  String  lendPerson;

    @Excel(name = "借出数量", width = 15)
    @ApiModelProperty(value = "借出数量")
    private  Integer  confirmNum;

    @Excel(name = "借出时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "借出时间")
    private  java.util.Date  outTime;

    @Excel(name = "借出部门", width = 15)
    @ApiModelProperty(value = "借出部门")
    private  String  outDepart;

    @Excel(name = "借出人", width = 15)
    @ApiModelProperty(value = "借出人")
    private  String  createBy;

    @Excel(name = "还回时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "还回时间")
    private  java.util.Date  backTime;

    @Excel(name = "还回数量", width = 15)
    @ApiModelProperty(value = "还回数量")
    private  Integer  backNum;

    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  remarks;

    /**状态（0-未还 1-已还）*/
    @ApiModelProperty(value = "状态（0-未还 1-已还）")
    private  Integer  status;

    /**状态*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private  String  statusDesc;

    @Excel(name = "借出班组", width = 15)
    @ApiModelProperty(value = "借出仓库")
    private  String  orgId;
}
