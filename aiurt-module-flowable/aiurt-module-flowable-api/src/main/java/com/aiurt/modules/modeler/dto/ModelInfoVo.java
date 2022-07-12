package com.aiurt.modules.modeler.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: flow
 * @description: 模型的VO
 * @author: fgw
 * @create: 2021-04-20 21:25
 **/
@Data
@ApiModel(value = "ModelInfoVo", description = "查询模型对象返回对象")
public class ModelInfoVo implements Serializable {
    private static final long serialVersionUID = -2434943659168309903L;
    @ApiModelProperty(value = "主键id")
    private String id;
    @ApiModelProperty(value = "模型id")
    private String modelId;

    @ApiModelProperty(value = "模型key")
    private String modelKey;

    @ApiModelProperty(value = "流程模板名称")
    private String modelName;

    private String fileName;

    @ApiModelProperty(value = "xml")
    private String modelXml;
    private String appSn;
    
    private String categoryCode;
}
