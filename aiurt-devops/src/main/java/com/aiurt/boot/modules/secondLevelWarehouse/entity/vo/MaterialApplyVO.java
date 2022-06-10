package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: km
 * DateTime: 2021/9/29 16:51
 */
@Data
public class MaterialApplyVO {
    @ApiModelProperty("备件申领物资详情的id")
    private Long id;

    @ApiModelProperty("备件编号")
    @NotNull(message = "物资编号不能为空")
    private String materialCode;

    @ApiModelProperty("数量")
    @NotNull(message = "数量不能为空")
    private Integer materialNum;
}
