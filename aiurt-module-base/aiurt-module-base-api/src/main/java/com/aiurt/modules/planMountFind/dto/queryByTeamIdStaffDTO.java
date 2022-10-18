package com.aiurt.modules.planMountFind.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author: Sand Sculpture King
 * @date: 2021年05月25日 15:16
 */
@Data
@ApiModel("通过TeamID查询身份信息")
public class queryByTeamIdStaffDTO implements Serializable {
    @ApiModelProperty(value = "施工负责人姓名")
    private String name;
    @ApiModelProperty(value = "施工负责人id")
    private String id;
}
