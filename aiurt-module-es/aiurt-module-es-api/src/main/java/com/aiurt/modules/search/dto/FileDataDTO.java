package com.aiurt.modules.search.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class FileDataDTO {
    /**
     * 记录ID
     */
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "记录ID")
    private String id;
    /**
     * 文件对象名称
     */
    @ApiModelProperty(value = "文件对象名称")
    private String name;
    /**
     * 文件格式
     */
    @ApiModelProperty(value = "文件类型")
    private String format;
    /**
     * 文件字节数组
     */
    @ApiModelProperty(value = "文件字节数组")
    private byte[] fileBytes;
    /**
     * 文件类型ID
     */
    @ApiModelProperty(value = "文件类型ID")
    private String tyepId;
    /**
     * 文件存储地址(即数据库存的地址)
     */
    @ApiModelProperty(value = "文件存储地址(即数据库存的地址)")
    private String address;
}
