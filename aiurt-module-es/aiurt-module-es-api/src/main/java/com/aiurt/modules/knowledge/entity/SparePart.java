package com.aiurt.modules.knowledge.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class SparePart {
    /**
     * 主键id
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**
     * 故障原因及解决方案表ID
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "故障原因及解决方案表ID")
    private String causeSolutionId;
    /**
     * 备件编码
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "备件编码")
    private String sparePartCode;
    /**
     * 备件名称
     */
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "备件名称")
    private String sparePartName;
    /**
     * 规格型号
     */
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "规格型号")
    private String specification;
    /**
     * 数量
     */
    @Field(type = FieldType.Integer)
    @ApiModelProperty(value = "数量")
    private Integer number;
}
