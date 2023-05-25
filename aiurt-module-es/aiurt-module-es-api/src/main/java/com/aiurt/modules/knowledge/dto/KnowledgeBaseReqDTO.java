package com.aiurt.modules.knowledge.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author
 * @description 故障知识库高级搜索参数DTO对象
 */
@ApiModel(value = "故障知识库高级搜索参数DTO对象", description = "故障知识库高级搜索参数DTO对象")
@Data
public class KnowledgeBaseReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 搜索内容
     */
    @ApiModelProperty(value = "搜索内容")
    private String keyword;

    /**
     * 专业编号
     */
    @ApiModelProperty(value = "专业编号")
    private String majorCode;

    /**
     * 子系统编号
     */
    @ApiModelProperty(value = "子系统编号")
    private String systemCode;

    /**
     * 设备类型编号
     */
    @ApiModelProperty(value = "设备类型编号")
    private String deviceTypeCode;

    /**
     * 组件编号
     */
    @ApiModelProperty(value = "组件编号")
    private String materialCode;

    /**
     * 排序:匹配程度传0,浏览次数传1
     */
    @ApiModelProperty(value = "排序:匹配程度传0,浏览次数传1")
    private Integer sort;
}
