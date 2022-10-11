package com.aiurt.modules.workticket.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("工作票图片上传")
public class UploadPictureDTO implements Serializable {

    @ApiModelProperty(name = "工作票id", required = true)
    private String id;

    @ApiModelProperty(name = "图片路径", required = true)
    private String[] path;
}
