package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/20
 */
@Data
public class AssortNumResult {

    @ApiModelProperty(value = "配合施工人次")
    private Integer assortNum;
}
