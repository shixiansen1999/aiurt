package com.aiurt.modules.search.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询公用响应结果DTO
 *
 * @author cgkj0
 */
@Data
public class CommonResponseDTO {
    @ApiModelProperty("标题")
    private String title;
}
