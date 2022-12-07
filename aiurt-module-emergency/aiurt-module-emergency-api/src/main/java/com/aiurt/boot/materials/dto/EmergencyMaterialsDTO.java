package com.aiurt.boot.materials.dto;


import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.EAN;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @author zwl
 */
@Data
public class EmergencyMaterialsDTO {

    /**应急物资巡检单号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "应急物资巡检单号")
    private java.lang.String materialsPatrolCode;
    /**巡视标准编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视标准编码")
    private java.lang.String standardCode;
    /**巡视标准名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视标准名称")
    private java.lang.String standardName;
    /**巡视线路编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视线路编码")
    private java.lang.String lineCode;
    /**巡视站点编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视站点编码")
    private java.lang.String stationCode;
    /**巡视位置编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视位置编码")
    private java.lang.String positionCode;
    /**巡视日期*/
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "巡视日期")
    private java.util.Date patrolDate;
    /**巡视人ID*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视人ID")
    private java.lang.String userId;

    /**应急物资巡检单检查项*/
    @TableField(exist = false)
    @ApiModelProperty(value = "应急物资巡检单检查项")
    private List<EmergencyMaterialsInvoicesItem> emergencyMaterialsInvoicesItemList;
}
