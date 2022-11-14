package com.aiurt.boot.weeklyplan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lai W.
 * @version 1.0
 */

@Data
public class BdUserInfoDTO {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "roleID")
    private String roleID;

    @ApiModelProperty(value = "roleName")
    private String roleName;

    @ApiModelProperty(value = "teamID")
    private String teamID;

    @ApiModelProperty(value = "teamName")
    private String teamName;

    @ApiModelProperty(value = "deptID")
    private String deptID;

    @ApiModelProperty(value = "deptName")
    private String deptName;

    @ApiModelProperty(value = "lineID")
    private String lineID;

}
