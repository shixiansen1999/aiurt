package com.aiurt.modules.planMountFind.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "老变电方法.获取type表中的remark列值")
public class getRoleTypeDTO {

    @ApiModelProperty(value = "remark列")
    String remark;

}
