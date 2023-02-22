package com.aiurt.modules.search.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title: 前端搜索参数统一封装
 * @Description:
 * @date 2023/2/148:40
 */
@Data
@ApiModel(value="前端搜索参数统一封装", description="前端搜索参数统一封装")
public class SearchRequestDTO extends CommonRequestDTO {

    @ApiModelProperty(value = "故障现象分类编码")
    private String knowledgeBaseTypeCode;

    @ApiModelProperty(value = "设备分类")
    private String deviceTypeCode;

    @ApiModelProperty(value = "设备组件")
    private String materialCode;

}
