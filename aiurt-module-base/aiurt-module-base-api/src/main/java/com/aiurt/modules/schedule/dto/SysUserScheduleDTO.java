package com.aiurt.modules.schedule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/911:15
 */
@Data
public class SysUserScheduleDTO {
    @ApiModelProperty(value = "值班类型")
    private String itemName;
    @ApiModelProperty(value = "排班人员姓名")
    private String userName;
    @ApiModelProperty(value = "排班人员id")
    private String userId;
    @ApiModelProperty(value = "联系电话")
    private String telephone;
    @ApiModelProperty(value = "角色")
    private String roleName;
    @ApiModelProperty(value = "所属工区")
    private String siteName;
    @ApiModelProperty(value = "工区位置")
    private String siteLocationName;
    @ApiModelProperty(value = "工区负责人名称")
    private String sitPrincipalName;


}
