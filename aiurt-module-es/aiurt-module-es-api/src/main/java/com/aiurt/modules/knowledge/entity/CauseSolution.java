package com.aiurt.modules.knowledge.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
public class CauseSolution {
    /**
     * 主键ID
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**
     * 故障知识库ID
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "故障知识库ID")
    private String knowledgeBaseId;
    /**
     * 故障原因
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    @CompletionField(analyzer = "ik_max_word")
    @ApiModelProperty(value = "故障原因")
    private String faultCause;
    /**
     * 解决方案
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    @CompletionField(analyzer = "ik_max_word")
    @ApiModelProperty(value = "解决方案")
    private String solution;
    /**
     * 维修视频url
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "维修视频url")
    private String videoUrl;
    /**
     * 原因出现率百分比
     */
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "原因出现率百分比")
    private String happenRate;
    /**
     * 备件信息
     */
    @Field(type = FieldType.Nested)
    @ApiModelProperty(value = "备件信息")
    private List<SparePart> spareParts;
}
