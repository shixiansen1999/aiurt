package com.aiurt.modules.largescream.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel("大屏详情")
public class LargeFaultDataDatailDTO implements Serializable {

    private static final long serialVersionUID = -9096219292781149632L;


    private String startDate;

    private String endDate;

    @ApiModelProperty(name = "faultModule", value = "故障数据统计详情模块：1:故障总数 2:未修复故障数 3:本周增加 4:本周修复 5:今日增加 6:今日修复,7:挂起数")
    private Integer faultModule = 1;

    @ApiModelProperty(name = "lineCode",value = "线路")
    private String lineCode;

    private Integer pageNo =1 ;

    private Integer pageSize = 10;
}
