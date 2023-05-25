package com.aiurt.modules.personnelportrait.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author
 * @description
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@ApiModel(value = "用户信息Model对象", description = "用户信息Model对象")
public class UserInfoResDTO implements Serializable {
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
     * 值班状态
     */
    @ApiModelProperty(value = "值班状态")
    private String dutyStatus;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String username;

    /**
     * 用户角色
     */
    @ApiModelProperty(value = "用户角色")
    private String role;

    /**
     * 用户职级
     */
    @ApiModelProperty(value = "用户职级")
    private String level;

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
}
