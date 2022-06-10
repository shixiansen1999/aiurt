package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author WangHongTao
 * @Date 2021/11/15
 */
@Data
public class SparePartReturnVO {

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

    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    private String materialName;

    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
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

    @Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    private String unit;

    @Excel(name = "所在班组", width = 15)
    @ApiModelProperty(value = "所在班组")
    private String orgId;

    @Excel(name = "退还数量", width = 15)
    @ApiModelProperty(value = "退还数量")
    private  Integer  num;

    @Excel(name = "退库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "退库时间")
    private  java.util.Date  returnTime;

    @Excel(name = "备注", width = 15)
    @ApiModelProperty("备注")
    private String remarks;

    @Excel(name = "操作人", width = 15)
    @ApiModelProperty(value = "创建人")
    private  String  createBy;

}
