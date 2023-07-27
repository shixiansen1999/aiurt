package com.aiurt.modules.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel("常用选择")
public class SysUserUsageRespDTO implements Serializable {


    @ApiModelProperty("用户id")
    private String userId;

    private String value;

    @ApiModelProperty("用户姓名")
    private String userName;

    private String title;


    @ApiModelProperty("机构id")
    private String orgId;

    @ApiModelProperty("所属部门")
    private String orgName;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("该纬度下的总人数")
    private Integer userNum;

    @ApiModelProperty("是否部门")
    private Boolean isOrg = false;

    @ApiModelProperty("是否岗位")
    private Boolean isPost = false;

    @ApiModelProperty("是否角色")
    private Boolean isRole = false;

    @ApiModelProperty("所属岗位，多个用逗号隔开")
    private String postName;

    @ApiModelProperty("所属角色，多个用逗号隔开")
    private String roleName;


}
