package com.aiurt.modules.knowledge.dto;

import com.aiurt.modules.knowledge.entity.CauseSolution;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author
 * @description 故障知识库高级搜索响应DTO对象
 */
@ApiModel(value = "故障知识库高级搜索响应DTO对象", description = "故障知识库高级搜索响应DTO对象")
@Data
public class KnowledgeBaseResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private String id;

    /**
     * 故障现象编码
     */
    @ApiModelProperty(value = "故障现象编码")
    private String faultPhenomenonCode;

    /**
     * 故障现象
     */
    @ApiModelProperty(value = "故障现象")
    private String title;

    /**
     * 故障知识分类编码
     */
    @ApiModelProperty(value = "故障知识分类编码")
    private String knowledgeBaseTypeCode;

    /**
     * 故障知识分类名称
     */
    @ApiModelProperty(value = "故障知识分类名称")
    private String knowledgeBaseTypeName;

    /**
     * 专业编号
     */
    @ApiModelProperty(value = "专业编号")
    private String majorCode;

    /**
     * 专业名称
     */
    @ApiModelProperty(value = "专业名称")
    private String majorName;

    /**
     * 子系统编号
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "子系统编号")
    private String systemCode;

    /**
     * 子系统名称
     */
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "子系统名称")
    private String systemName;

    /**
     * 组件部位编号
     */
    @ApiModelProperty(value = "组件部位编号")
    private String materialCode;

    /**
     * 组件部位名称
     */
    @ApiModelProperty(value = "组件部位名称")
    private String materialName;

    /**
     * 设备类型编号
     */
    @ApiModelProperty(value = "设备类型编号")
    private String deviceTypeCode;

    /**
     * 设备类型名称
     */
    @ApiModelProperty(value = "设备类型名称")
    private String deviceTypeName;

    /**
     * 故障原因
     */
    @ApiModelProperty(value = "故障原因")
    private List<CauseSolution> reasonSolutions;

    /**
     * 故障等级编号
     */
    @ApiModelProperty(value = "故障等级编号")
    private String faultLevelCode;

    /**
     * 故障等级名称
     */
    @ApiModelProperty(value = "故障等级名称")
    private String faultLevelName;

    /**
     * 浏览数
     */
    @ApiModelProperty(value = "浏览数")
    private Integer scanNum;

    /**
     * 排查方法
     */
    @ApiModelProperty(value = "排查方法")
    private String method;

    /**
     * 采用数
     */
    @ApiModelProperty(value = "采用数")
    private Integer use;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
