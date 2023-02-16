package com.aiurt.modules.search.entity;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author cgkj
 * @Title:
 * @Description: 文件数据对象
 * @date 2023/2/159:11
 */
@Data
@ApiModel(value = "文件数据对象", description = "文件数据对象")
public class FileAnalysisData  {

    /**
     * 记录ID
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "记录ID")
    private String id;
    /**
     * 文件对象名称
     */
    @Field(type = FieldType.Text, searchAnalyzer = "ik_max_word", analyzer = "ik_smart")
    @ApiModelProperty(value = "文件对象名称")
    private String name;
    /**
     * 文件格式
     */
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "文件类型")
    private String format;
    /**
     * 文件类型
     */
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "文件类型")
    @Dict(dictTable = "sys_file_type", dicText = "name", dicCode = "id")
    private String typeId;
    /**
     * 64位编码的文件内容
     */
    @Field(type = FieldType.Text, searchAnalyzer = "ik_max_word", analyzer = "ik_smart")
    @ApiModelProperty(value = "64位编码的文件内容")
    private String content;
    /**
     * 文件存储地址
     */
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "文件存储地址")
    private String address;

}
