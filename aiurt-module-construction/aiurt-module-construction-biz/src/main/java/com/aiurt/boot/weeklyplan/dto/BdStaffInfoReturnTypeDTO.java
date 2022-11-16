package com.aiurt.boot.weeklyplan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Lai W.
 * @version 1.0
 */

@Data
public class BdStaffInfoReturnTypeDTO {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "RoleId")
    private String roleId;

    @ApiModelProperty(value = "TeamId")
    private String teamId;

    @ApiModelProperty(value = "电话号码")
    private String phoneNo;

//    @ApiModelProperty(value = "位置X")
//    private Double positionX;
//
//    @ApiModelProperty(value = "位置Y")
//    private Double positionY;

    @ApiModelProperty(value = "线路id")
//    private String lineId;
    private List<String> lineIds;

}
