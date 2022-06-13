package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.common.api.vo.PageVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: km
 * DateTime: 2021/9/29 10:30
 */
@Data
public class StockLevel2Query extends PageVO {
    @ApiModelProperty(value = "物资编号")
    private String materialCode;

    @ApiModelProperty(value = "物资名称")
    private String materialName;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;

    @ApiModelProperty(value = "所属系统")
    private String systemCode;

    @ApiModelProperty(value = "所属部门")
    private String department;

    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
