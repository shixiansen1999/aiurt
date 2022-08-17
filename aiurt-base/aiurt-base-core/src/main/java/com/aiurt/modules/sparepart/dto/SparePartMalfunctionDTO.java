package com.aiurt.modules.sparepart.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fgw
 */
@Data
public class SparePartMalfunctionDTO implements Serializable {

    private static final long serialVersionUID = -4180105265227492110L;

    /**出库记录表ID*/
    @ApiModelProperty(value = "出库记录表ID")
    private String outOrderId;

    /**维修记录单号*/
    @ApiModelProperty(value = "维修记录单号")
    private String maintenanceRecord;

    /**故障设备编号*/
    @ApiModelProperty(value = "故障设备编号")
    private String malfunctionDeviceCode;


    /**故障类别：1设备故障、2外界妨害、3其他*/
    @ApiModelProperty(value = "故障类别：1设备故障、2外界妨害、3其他")
    private Integer malfunctionType;

    /**详细描述*/
    @ApiModelProperty(value = "详细描述")
    private String description;
    /**替换数量*/
    @Excel(name = "替换数量", width = 15,mergeVertical = true)
    @ApiModelProperty(value = "替换数量")
    private Integer replaceNumber;

    /**维修机构ID*/
    @ApiModelProperty(value = "维修机构ID")
    private String orgId;

    /**维修用戶ID*/
    @ApiModelProperty(value = "维修用戶ID")
    private String maintainUserId;

    /**维修时间*/
    @ApiModelProperty(value = "维修时间")
    private Date maintainTime;


    /**删除状态： 0未删除 1已删除*/
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
}
