package com.aiurt.modules.search.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 规范知识库统一搜索条件
 * @date 2023/2/1516:22
 */
@Data
public class DocumentManageRequestDTO extends CommonRequestDTO {

    @ApiModelProperty(value = "文件格式")
    private String format;

    @ApiModelProperty(value = "文件类型")
    private String typeId;
}
