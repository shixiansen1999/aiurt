package com.aiurt.boot.monthlyplan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: TODO
 * @author: Sand Sculpture King
 * @date: 2021年05月25日 15:28
 */
@Data
@ApiModel(value = "生产经理 与 线路负责人")
public class queryStaffsByRoleTypeDTO {
    @ApiModelProperty("人员id")
    private String id;
    @ApiModelProperty("人员姓名")
    private String name;
}
