package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.common.api.vo.PageVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Author km
 * @Date 2021/9/24 17:20
 * @Version 1.0
 */
@Data
public class SparePartQuery extends PageVO {
    @ApiModelProperty("申领单号/出库单号")
    private  String  code;

    /**申领仓库 备件库*/
    @ApiModelProperty(value = "申领仓库 备件库")
    private  String  warehouseCode;

    /**出库仓库 二级库*/
    @ApiModelProperty(value = "出库仓库 二级库")
    private  String  outWarehouseCode;


    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申领开始时间")
    private  java.util.Date  applyTimeStart;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申领结束时间")
    private  java.util.Date  applyTimeEnd;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "出库开始时间")
    private  java.util.Date  stockOutStart;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "出库结束时间")
    private  java.util.Date  stockOutEnd;

    @ApiModelProperty("提交状态（0-未提交 1-已提交）")
    private Integer commitStatus;

    @ApiModelProperty("ids")
    List<Integer> selections;
}
