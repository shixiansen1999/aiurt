package com.aiurt.modules.faultknowledgebase.dto;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel(value = "查询故障现象模板实体类")
public class SymptomResDTO implements Serializable {

    private static final long serialVersionUID = 2214680771943628031L;

    @ApiModelProperty(value = "主键id")
    private String id;


    @ApiModelProperty(value = "专业编码，字典值")
    @Dict(dicCode = "major_code", dicText = "major_name", dictTable = "cs_major")
    private String majorCode;

    @ApiModelProperty(value = "子系统编码，字典值")
    @Dict(dicCode = "system_code", dicText = "system_name", dictTable = "cs_subsystem")
    private String systemCode;

    @ApiModelProperty(value = "设备类型编码，字典值")
    @Dict(dicCode = "code",dictTable = "device_type", dicText = "name")
    private String deviceTypeCode;

    @ApiModelProperty(value = "组件编码, 字典值")
    @Dict(dicCode = "code",dictTable = "material_base", dicText = "name")
    private String materialCode;

    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    @ApiModelProperty(value = "故障现象分类编码，字典值")
    @Dict(dicCode = "code",dictTable = "fault_knowledge_base_type", dicText = "name")
    private String knowledgeBaseTypeCode;

    @ApiModelProperty(value = "故障等级编码，字典值")
    @Dict(dicCode = "code",dictTable = "fault_level", dicText = "name")
    private String faultLevelCode;

}
