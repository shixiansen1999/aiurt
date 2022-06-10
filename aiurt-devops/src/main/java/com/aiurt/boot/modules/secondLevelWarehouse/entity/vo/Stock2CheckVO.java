package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author km
 * @Date 2021/9/18 10:50
 * @Version 1.0
 */
@Data
public class Stock2CheckVO {
    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty(value = "盘点任务单号")
    private  String  stockCheckCode;


    @ApiModelProperty(value = "盘点仓库编号")
    private  String  warehouseCode;

    @ApiModelProperty(value = "盘点仓库名称")
    private  String  warehouseName;

    @ApiModelProperty(value = "盘点数量")
    private  Integer  checkAllNum;

    @ApiModelProperty("仓库所属部门")
    private String warehouseDepartment;

    @ApiModelProperty("盘点人名称")
    private String checkerName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "盘点开始时间")
    private  java.util.Date  checkStartTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "盘点结束时间")
    private  java.util.Date  checkEndTime;

    @ApiModelProperty(value = "备注")
    private  String  note;
}
