package com.aiurt.modules.faultknowledgebase.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fgw
 */
@Data
public class RepairSolutionResDTO extends DictEntity {

    private static final long serialVersionUID = 5203688984744542169L;

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

    @ApiModelProperty(value = "物料主数据 字典值")
    @Dict(dicCode = "code",dictTable = "material_base", dicText = "name")
    private String materialCode;

    @ApiModelProperty(value = "组件-部位")
    private String materialName;

    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    @ApiModelProperty(value = "故障现象分类编码，字典值")
    @Dict(dicCode = "code",dictTable = "fault_knowledge_base_type", dicText = "name")
    private String knowledgeBaseTypeCode;

    @ApiModelProperty(value = "故障等级编码，字典值")
    @Dict(dicCode = "code",dictTable = "fault_level", dicText = "name")
    private String faultLevelCode;


    private String baseTypeName;

    private Integer memberNumber;

    private Boolean isFirst;

    private Integer group;
}
