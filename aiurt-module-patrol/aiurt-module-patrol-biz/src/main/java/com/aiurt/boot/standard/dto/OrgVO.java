package com.aiurt.boot.standard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2023/2/20
 * @desc
 */
@Data
public class OrgVO {
    @ApiModelProperty(value = "机构编码")
    private String label ;
    @ApiModelProperty(value = "机构名称")
    private String value;
}
