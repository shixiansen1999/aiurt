package com.aiurt.modules.faultknowledgebase.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

    @ApiModelProperty(value = "故障原因")
    private List<AnalyzeFaultCauseResDTO> analyzeFaultCauseResDTOList;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
