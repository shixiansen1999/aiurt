package com.aiurt.modules.maplocation.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2021/4/3015:03
 */
@Data
public class UserInfo
{
    @ApiModelProperty(value = "用户id")
    private String id;
    @ApiModelProperty(value = "用户名称")
    private String name;
    @ApiModelProperty(value = "账号")
    private String userName;
    @ApiModelProperty(value = "电话")
    private String phone;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "位置X")
    private Double positionX;
    @ApiModelProperty(value = "位置Y")
    private Double positionY;
    @ApiModelProperty(value = "安全证书")
    private String safeLevel;
    @ApiModelProperty(value = "证件编码")
    private String identityNumber;
    @ApiModelProperty(value = "工号")
    private String workNo;
}
