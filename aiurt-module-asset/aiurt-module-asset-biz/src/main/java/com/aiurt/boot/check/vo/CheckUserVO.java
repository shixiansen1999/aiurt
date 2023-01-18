package com.aiurt.boot.check.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 盘点任务记录列表盘点人信息VO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "盘点任务记录列表盘点人信息VO对象", description = "盘点任务记录列表盘点人信息VO对象")
public class CheckUserVO {
    /**
     * 盘点人ID
     */
    @ApiModelProperty(value = "盘点人ID")
    private String checkId;
    /**
     * 盘点人名称
     */
    @ApiModelProperty(value = "盘点人名称")
    private String checkName;
}
