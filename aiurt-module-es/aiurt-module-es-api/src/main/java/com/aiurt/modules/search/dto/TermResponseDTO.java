package com.aiurt.modules.search.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 词语补全提示返回结果dto
 * @date 2023/2/1414:22
 */
@Data
public class TermResponseDTO {
    @ApiModelProperty("词语")
    private String key;

    public TermResponseDTO(String key) {
        this.key = key;
    }
}
