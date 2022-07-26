package com.aiurt.modules.modeler.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @author fgw
 */
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormOperation implements Serializable {

    private static final long serialVersionUID = 1096075702520208827L;

    @ApiModelProperty(value = "按钮id")
    private String id;

    @ApiModelProperty(value = "按钮名称")
    private String label;

    @ApiModelProperty(value = "按钮展示顺序")
    private Integer showOrder;

    @ApiModelProperty(value = "按钮类型")
    private String type;
}
