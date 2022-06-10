package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renanfeng
 * @version 1.0
 * @date 2022/01/25 13:30
 */
@Data
public class StatisticsFaultStatusVO {
    @ApiModelProperty(value = "状态名称")
    private String status;
    @ApiModelProperty(value = "数量")
    private int count;
}
