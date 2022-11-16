package com.aiurt.modules.maplocation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("指派人员")
public class AssignUserDTO implements Serializable {

    private static final long serialVersionUID = -4444924821335955556L;

    @ApiModelProperty("用户id")
    private String id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("真实名")
    private String realname;

    @ApiModelProperty(value = "在岗状态")
    private String status;

    @ApiModelProperty(value = "班组")
    private String teamName;
    private Integer num;
    private String name;
    @ApiModelProperty(value = "附近站点位置")
    private String stationName;

}
