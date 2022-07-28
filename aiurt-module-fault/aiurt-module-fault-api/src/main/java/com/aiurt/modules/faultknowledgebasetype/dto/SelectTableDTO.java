package com.aiurt.modules.faultknowledgebasetype.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("知识库下拉列表")
public class SelectTableDTO {

    private String id;

    private String key;

    private String value;

    private String label;

    private String pid;

    private String systemCode;

    private String majorCode;

    List<SelectTableDTO> children;

    /**是否是知识库类别*/
    private Boolean isBaseType;
}
