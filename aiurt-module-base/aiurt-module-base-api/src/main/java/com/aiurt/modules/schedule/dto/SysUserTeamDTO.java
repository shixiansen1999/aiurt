package com.aiurt.modules.schedule.dto;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1416:31
 */
@Data
public class SysUserTeamDTO {
    @ApiModelProperty(value = "姓名")
    private String realName;

    @ApiModelProperty(value = "用户账号")
    private String username;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "角色")
    private String roleName;

    @ApiModelProperty(value = "岗位")
    @Dict(dicCode = "sys_post")
    private String jobName;

    @ApiModelProperty(value = "班组")
    private String teamName;

    @ApiModelProperty(value = "施工证编号")
    private String permitCode;

    @ApiModelProperty(value = "工资编号")
    private String salaryCode;

    @ApiModelProperty(value = "班次名称")
    private String scheduleItemName;

    @ApiModelProperty("工作证编号")
    private String cardCode;
}
