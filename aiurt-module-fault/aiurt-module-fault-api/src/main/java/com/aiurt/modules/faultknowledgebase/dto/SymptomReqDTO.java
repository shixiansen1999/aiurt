package com.aiurt.modules.faultknowledgebase.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fgw
 * @desc 查询故障现象模板
 */
@Data
@ApiModel(value = "查询故障现象模板实体类")
public class SymptomReqDTO extends BaseEntity {

    @ApiModelProperty(value = "专业编码")
    private String majorCode;

    @ApiModelProperty(value = "子系统编码")
    private String systemCode;

    @ApiModelProperty(value = "设备类型编码")
    private String deviceTypeCode;

    @ApiModelProperty(value = "组件编码")
    private String materialCode;

    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    @ApiModelProperty(value = "故障现象分类编码")
    private String knowledgeBaseTypeCode;

    @ApiModelProperty(value = "故障等级编码")
    private String faultLevelCode;
}
