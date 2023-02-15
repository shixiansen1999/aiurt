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
public class SearchRequestDTO {

    @ApiModelProperty("匹配关键词")
    private String keyword;

    /**
     * sort=createTime_desc
     * sort=createTime_asc,updateTime_desc
     */
    @ApiModelProperty("排序条件,多列排序使用,隔开")
    private String sort;

    @ApiModelProperty(value = "故障现象分类编码")
    private String knowledgeBaseTypeCode;

    @ApiModelProperty(value = "设备分类")
    private String deviceTypeCode;

    @ApiModelProperty(value = "设备组件")
    private String materialCode;

    @ApiModelProperty(value = "当前页")
    private Integer pageNo;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;
}
