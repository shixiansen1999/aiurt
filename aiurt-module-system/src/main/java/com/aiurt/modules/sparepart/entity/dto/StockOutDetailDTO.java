package com.aiurt.modules.sparepart.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/17 20:07
 * @Version 1.0
 */
@Data
public class StockOutDetailDTO {
    @ApiModelProperty("物资详情的id")
    private Long id;

    @ApiModelProperty("物资编号")
    private String materialCode;
    @ApiModelProperty("物资数量")
    private Integer materialNum;

}
