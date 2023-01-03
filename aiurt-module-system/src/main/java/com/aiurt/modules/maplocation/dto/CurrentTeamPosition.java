package com.aiurt.modules.maplocation.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2021/4/3010:12
 */
@Data
public class CurrentTeamPosition {
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "班组id")
    private String teamId;
    @ApiModelProperty(value = "用户名称")
    private String name;
    @ApiModelProperty(value = "用户账号")
    private String username;
    @ApiModelProperty(value = "位置X")
    private Double positionX;
    @ApiModelProperty(value = "位置Y")
    private Double positionY;
    @ApiModelProperty(value = "最近更新时间")
    private String positionUpdateTime;
    /**
     * 报警类型（0未登录系统（非报错无alarm）   1离线      2越界      3迟到      4(正常（正常）)）
     */
    @ApiModelProperty(value = "状态id")
    private String currentStaffStatusId;
    @ApiModelProperty(value = "状态名称")
    private String currentStaffStatusName;
    @ApiModelProperty("标识是站点还是人员,0是人员，1是站点")
    private Integer flag;
    @ApiModelProperty("站点id")
    private String stationId;
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    @ApiModelProperty(value = "证件编号")
    private String identityNumber;
    @ApiModelProperty(value = "安全登记")
    private String securityLevel;
    @ApiModelProperty(value = "手机号码")
    private String phone;

    private String mac;
}
