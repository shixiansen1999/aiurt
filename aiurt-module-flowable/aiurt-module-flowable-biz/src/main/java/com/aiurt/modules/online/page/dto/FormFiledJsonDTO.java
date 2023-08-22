package com.aiurt.modules.online.page.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author:wgp
 * @create: 2023-08-21 09:17
 * @Description:
 */
@Data
public class FormFiledJsonDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;
    @ApiModelProperty(value = "字段英文名")
    private String fieldValue;
    @ApiModelProperty(value = "字段类型：0-主表字段，1-子表字段")
    private Integer fieldType;
    @ApiModelProperty(value = "是否显示：true显示，false隐藏")
    private Boolean isDisplay;
    @ApiModelProperty(value = "是否可编辑：true可编辑，false只读")
    private Boolean isEdit;

}
