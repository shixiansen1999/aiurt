package com.aiurt.modules.stock.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 二级库请求DTO，目前只用作更新备注，后续再添加其他
 *
 * @author 华宜威
 * @date 2023-09-22 15:19:47
 */
@Data
public class StockLevel2ReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键id*/
    @ApiModelProperty(value = "主键id")
    private String id;

    /**备注*/
    @ApiModelProperty(value = "备注")
    private String remark;

}
