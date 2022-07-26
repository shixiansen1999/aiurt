package com.aiurt.modules.train.task.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
public class UserDTO {

    @ApiModelProperty(value = "用户id")
    private String id;
    @Excel(name = "用户账号", width = 15)
    @ApiModelProperty(value = "用户账号")
    private String username;

    @Excel(name = "用户姓名")
    @ApiModelProperty(value = "用户姓名")
    private String realname;

    @Excel(name = "部门")
    @ApiModelProperty(value = "部门")
    private String orgName;

    @Excel(name = "职务")
    @ApiModelProperty(value = "职务")
    private String post;

    @Excel(name = "工区")
    @ApiModelProperty(value = "工区")
    private String siteName;

    @Excel(name = "机构")
    @ApiModelProperty(value = "机构")
    private String teamName;

    @Excel(name = "角色")
    @ApiModelProperty(value = "角色")
    private String roleNames;

    private List<String> roleName;

    @Excel(name = "电话")
    @ApiModelProperty(value = "电话")
    private String phone;

    @Excel(name = "证件编号")
    @ApiModelProperty(value = "证件编号")
    private String identityNumber;

    @Excel(name = "安全等级")
    @ApiModelProperty(value = "安全等级")
    private String safeLevel;

    @Excel(name = "账号状态")
    @ApiModelProperty(value = "账号状态")
    private String state;

    @ApiModelProperty("班组id")
    private Integer teamId;

    @ApiModelProperty("角色id")
    private String roleId;
}
