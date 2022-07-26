package com.aiurt.modules.modeler.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class OperationList implements Serializable {

    private static final long serialVersionUID = 3382674873592157739L;

    @ApiModelProperty
    private List<FormOperation> formOperationList;
}
