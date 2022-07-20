package com.aiurt.modules.sparepart.entity.dto;

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

    /**备件名称*/
    @Excel(name = "备件名称", width = 15)
    @ApiModelProperty(value = "备件名称")
    private  String  materialName;

    @ApiModelProperty(value = "备件类型")
    private  Integer  type;
    /**备件类型名称*/
    @Excel(name = "备件类型", width = 15)
    @ApiModelProperty(value = "备件类型")
    private  String  typeName;

    /**规格&型号*/
    @Excel(name = "规格&型号", width = 15)
    @ApiModelProperty(value = "规格&型号")
    private String specifications;

    /**原产地*/
    @Excel(name = "原产地", width = 15)
    @ApiModelProperty(value = "原产地")
    private String countryOrigin;
    /**制造商*/
    @Excel(name = "制造商", width = 15)
    @ApiModelProperty(value = "制造商")
    private String manufacturer;

    /**报废数量*/
    @Excel(name = "报废数量", width = 15)
    @ApiModelProperty(value = "报废数量")
    private  Integer  num;

    @ApiModelProperty(value = "状态")
    private  Integer  status;
    /**状态*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private  String  statusString;

    /**报损时间*/
    @Excel(name = "报损时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "报损时间")
    private  java.util.Date  createTime;

    /**报损原因*/
    @Excel(name = "报损原因", width = 25)
    @ApiModelProperty(value = "报损原因")
    private  String  reason;
}
