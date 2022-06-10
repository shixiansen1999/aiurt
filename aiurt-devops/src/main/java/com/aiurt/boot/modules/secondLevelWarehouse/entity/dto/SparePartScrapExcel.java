package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author km
 * @Date 2021/9/23 17:41
 * @Version 1.0
 */
@Data
public class SparePartScrapExcel {

    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private  String  materialCode;

    @Excel(name = "备件名称", width = 15)
    @ApiModelProperty(value = "备件名称")
    private  String  materialName;

//    @Excel(name = "线路", width = 15)
    @ApiModelProperty("线路名称")
    private String lineName;

//    @Excel(name = "站点", width = 15)
    @ApiModelProperty("站点名称")
    private String stationName;

    @ApiModelProperty(value = "备件类型")
    private  Integer  type;

    @Excel(name = "备件类型", width = 15)
    @ApiModelProperty(value = "备件类型")
    private  String  typeName;

    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String specifications;

    @Excel(name = "生产厂家", width = 15)
    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @Excel(name = "报废数量", width = 15)
    @ApiModelProperty(value = "报废数量")
    private  Integer  num;

    @ApiModelProperty(value = "状态")
    private  Integer  status;

    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private  String  statusDesc;

    @Excel(name = "报损时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "报损时间")
    private  java.util.Date  scrapTime;

    @Excel(name = "更换人", width = 15)
    @ApiModelProperty(value = "更换人")
    private  String  createBy;

    @Excel(name = "报损原因", width = 25)
    @ApiModelProperty(value = "报损原因")
    private  String  reason;

//    @Excel(name = "所在班组", width = 15)
    @ApiModelProperty(value = "所在班组")
    private  String  orgId;
}
