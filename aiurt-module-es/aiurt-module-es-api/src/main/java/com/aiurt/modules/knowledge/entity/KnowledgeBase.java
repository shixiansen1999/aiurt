package com.aiurt.modules.knowledge.entity;

import com.aiurt.boot.annotation.ElasticId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author
 * @description 故障知识库高级搜索存储实体对象
 */
@Document(indexName = "knowledge_base")
@ApiModel(value = "故障知识库高级搜索存储实体对象", description = "故障知识库高级搜索存储实体对象")
@Data
public class KnowledgeBase {

    /**
     * 主键ID
     */
    @ElasticId
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "主键ID")
    private String id;

    /**
     * 故障现象编码
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "故障现象编码")
    private String faultPhenomenonCode;

    /**
     * 故障现象
     */
    @HighlightField(name = "faultPhenomenon")
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    @CompletionField(analyzer = "ik_max_word")
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    /**
     * 故障知识分类编码
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "故障知识分类编码")
    private String knowledgeBaseTypeCode;

    /**
     * 故障知识分类名称
     */
    @HighlightField(name = "knowledgeBaseTypeName")
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    @CompletionField(analyzer = "ik_max_word")
    @ApiModelProperty(value = "故障知识分类名称")
    private String knowledgeBaseTypeName;

    /**
     * 专业编号
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "专业编号")
    private String majorCode;

    /**
     * 专业名称
     */
    @HighlightField(name = "majorName")
    @Field(type = FieldType.Text)
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
    @HighlightField(name = "systemName")
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "子系统名称")
    private String systemName;

    /**
     * 组件部位编号
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "组件部位编号")
    private String materialCode;

    /**
     * 组件部位名称
     */
    @HighlightField(name = "materialName")
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "组件部位名称")
    private String materialName;

    /**
     * 设备类型编号
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "设备类型编号")
    private String deviceTypeCode;

    /**
     * 设备类型名称
     */
    @HighlightField(name = "deviceTypeName")
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "设备类型名称")
    private String deviceTypeName;

    /**
     * 故障原因
     */
    @Field(type = FieldType.Nested)
    @ApiModelProperty(value = "故障原因")
    private List<CauseSolution> reasonSolutions;

    /**
     * 故障等级编号
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "故障等级编号")
    private String faultLevelCode;

    /**
     * 故障等级名称
     */
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "故障等级名称")
    private String faultLevelName;

    /**
     * 浏览数
     */
    @Field(type = FieldType.Integer)
    @ApiModelProperty(value = "浏览数")
    private Integer scanNum;

    /**
     * 采用数
     */
    @Field(type = FieldType.Integer)
    @ApiModelProperty(value = "采用数")
    private Integer use;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
