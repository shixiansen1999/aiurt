package com.aiurt.modules.sparepart.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author km
 * @Date 2021/9/17 19:44
 * @Version 1.0
 */
@Data
public class StockOutDTO {
    @ApiModelProperty("出库单的id")
    private Long id;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("物资实际出库列表")
    private List<StockOutDetailDTO> materialVOList;
}
