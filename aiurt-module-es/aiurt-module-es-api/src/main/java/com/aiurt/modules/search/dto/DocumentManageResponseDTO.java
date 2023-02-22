package com.aiurt.modules.search.dto;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/1617:12
 */
@Data
public class DocumentManageResponseDTO {
    /**
     * 记录ID
     */
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
     * 文件类型
     */
    @ApiModelProperty(value = "文件类型")
    @Dict(dictTable = "sys_file_type", dicText = "name", dicCode = "id")
    private String typeId;
    /**
     * 文件存储地址
     */
    @ApiModelProperty(value = "文件存储地址")
    private String address;
    /**
     * 文件内容
     */
    @ApiModelProperty(value = "文件内容")
    private AttachmentDTO attachment;

}
