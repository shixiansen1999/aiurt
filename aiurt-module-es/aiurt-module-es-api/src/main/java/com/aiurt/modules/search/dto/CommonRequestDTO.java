package com.aiurt.modules.search.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 通用查询字段
 * @date 2023/2/1516:25
 */
@Data
public class CommonRequestDTO {
    @ApiModelProperty("匹配关键词")
    private String keyword;
    /**
     * sort=createTime_desc
     * sort=createTime_asc,updateTime_desc
     */
    @ApiModelProperty("排序条件,多列排序使用,隔开")
    private String sort;

    @ApiModelProperty(value = "当前页")
    private Integer pageNo;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;
}
