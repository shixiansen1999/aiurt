package com.aiurt.modules.modeler.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel(value = "测试")
public class ActOperationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "按钮名称")
    private String label;

    @ApiModelProperty(value = "按钮类型")
    private String type;

    @ApiModelProperty(value = "按钮展示顺序")
    private Integer showOrder;

    @ApiModelProperty(value = "按钮演示")
    private String  color;

    @ApiModelProperty(value = "是否有备注")
    private Boolean hasRemark;

    @ApiModelProperty(value = "备注是否必填")
    private Boolean mustRemark;

    @ApiModelProperty(value = "是否二次确认")
    private Boolean secordEnsure;
}
