package com.aiurt.modules.personnelportrait.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author
 * @description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户详情信息", description = "用户详情信息")
public class UserDetailResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;
    /**
     * 用户头像URL
     */
    @ApiModelProperty(value = "用户头像URL")
    private String picurl;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String username;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别")
    private String gender;

//    /**
//     * 用户年龄
//     */
//    @ApiModelProperty(value = "用户年龄")
//    private Integer age;

    /**
     * 入职年限
     */
    @ApiModelProperty(value = "入职年限")
    private Long year;

    /**
     * 用户工龄
     */
    @ApiModelProperty(value = "用户工龄")
    private String seniority;

    /**
     * 用户专业特长
     */
    @ApiModelProperty(value = "用户专业特长")
    private String speciality;

    /**
     * 用户专业编号
     */
    @ApiModelProperty(value = "用户专业编号")
    private String majorCode;

    /**
     * 用户专业名称
     */
    @ApiModelProperty(value = "用户专业名称")
    private String majorName;

    /**
     * 用户部门编号
     */
    @ApiModelProperty(value = "用户部门编号")
    private String orgCode;

    /**
     * 用户部门名称(专业名/部门名)
     */
    @ApiModelProperty(value = "用户部门名称")
    private String orgName;
    /**
     * 用户角色编号
     */
    @ApiModelProperty(value = "用户角色编号")
    private String roleCode;

    /**
     * 用户角色名称
     */
    @ApiModelProperty(value = "用户角色名称")
    private String roleName;

    /**
     * 用户职级
     */
    @ApiModelProperty(value = "用户职级")
    private String level;

    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    private String jobNumber;

    /**
     * 施工证编号
     */
    @ApiModelProperty(value = "施工证编号")
    private String certificateNumber;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String phone;

    /**
     * 岗位(角色名-用户职级)
     */
    @ApiModelProperty(value = "岗位")
    private String post;
}
