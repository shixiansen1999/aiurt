package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renanfeng
 * @version 1.0
 * @date 2022/01/25 13:30
 */
@Data
public class StatisticsFaultSystemVO {
    @ApiModelProperty(value = "系统名称")
    private String systemName;
    @ApiModelProperty(value = "故障数")
    private int count;
}
