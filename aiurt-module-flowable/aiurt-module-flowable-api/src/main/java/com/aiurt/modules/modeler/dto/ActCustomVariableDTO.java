package com.aiurt.modules.modeler.dto;

import com.aiurt.modules.common.entity.BaseSelectTable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class ActCustomVariableDTO extends BaseSelectTable implements Serializable {

    private static final long serialVersionUID = 7297981939964211123L;

    @ApiModelProperty(value = "是否系统字段, 1是，0否")
    private Integer isSystem;

    @ApiModelProperty(value = "类型， dept：机构， role：角色")
    private String variableType;
}
