package com.aiurt.modules.system.entity;

import java.util.Date;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 登录账号
     */
    @ApiModelProperty(value = "登录账号")
    @Excel(name = "登录账号", width = 15)
    private String username;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    @Excel(name = "真实姓名", width = 15)
    private String realname;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * md5密码盐
     */
    @ApiModelProperty(value = "md5密码盐")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    @Excel(name = "头像", width = 15,type = 2)
    private String avatar;

    /**
     * 生日
     */
    @ApiModelProperty(value = "生日")
    @Excel(name = "生日", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    @ApiModelProperty(value = "性别（1：男 2：女）")
    @Excel(name = "性别", width = 15,dicCode="sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * 电子邮件
     */
    @ApiModelProperty(value = "电子邮件")
    @Excel(name = "电子邮件", width = 15)
    private String email;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    @Excel(name = "电话", width = 15)
    private String phone;
    /**
     * 部门id(当前选择登录部门)
     */
    @ApiModelProperty(value = " 部门id(当前选择登录部门)")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    private String orgId;
    /**
     * 部门code(当前选择登录部门)
     */
    @ApiModelProperty(value = " 部门code(当前选择登录部门)")
    private String orgCode;
    /**
     * 部门name(当前选择登录部门)
     */
    @ApiModelProperty(value = " 部门name(当前选择登录部门)")
    private String orgName;

    /**部门名称*/
    @ApiModelProperty(value = " 部门名称")
    private transient String orgCodeTxt;

    /**
     * 状态(1：正常  2：冻结 ）
     */
    @ApiModelProperty(value = "状态(1：正常  2：冻结 ）")
    @Excel(name = "状态", width = 15,dicCode="user_status")
    @Dict(dicCode = "user_status")
    private Integer status;

    /**
     * 删除状态（0，正常，1已删除）
     */
    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
    @Excel(name = "删除状态", width = 15,dicCode="del_flag")
    @TableLogic
    private Integer delFlag;

    /**
     * 工号，唯一键
     */
    @ApiModelProperty(value = "工号，唯一键")
    @Excel(name = "工号", width = 15)
    private String workNo;

    /**
     * 职务，关联职务表
     */
    @ApiModelProperty(value = "职务，关联职务表")
    @Excel(name = "职务", width = 15)
    @Dict(dictTable ="sys_position",dicText = "name",dicCode = "code")
    private String post;

    /**
     * 座机号
     */
    @ApiModelProperty(value = "座机号")
    @Excel(name = "座机号", width = 15)
    private String telephone;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**
     * 同步工作流引擎1同步0不同步
     */
    @ApiModelProperty(value = "同步工作流引擎1同步0不同步")
    private Integer activitiSync;

    /**
     * 身份（0 普通成员 1 上级）
     */
    @ApiModelProperty(value = "身份（0 普通成员 1 上级）")
    @Excel(name="（1普通成员 2上级）",width = 15)
    private Integer userIdentity;

    /**
     * 负责部门
     */
    @ApiModelProperty(value = "负责部门")
    @Excel(name="负责部门",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    private String departIds;

    /**
     * 多租户id配置，编辑用户的时候设置
     */
    @ApiModelProperty(value = "多租户id配置")
    private String relTenantIds;

    /**设备id uniapp推送用*/
    @ApiModelProperty(value = "设备id")
    private String clientId;

    /**系统权限*/
    @ApiModelProperty(value = "系统权限codes")
    @TableField(exist = false)
    private List<String> systemCodes;

    /**角色*/
    @ApiModelProperty(value = "角色Ids")
    @TableField(exist = false)
    private List<String> roleIds;

    /**专业*/
    @ApiModelProperty(value = "专业Ids")
    @TableField(exist = false)
    private List<String> majorIds;

    /**部门*/
    @ApiModelProperty(value = "部门权限Codes")
    @TableField(exist = false)
    private List<String> departCodes;

    /**站点*/
    @ApiModelProperty(value = "站所Ids")
    @TableField(exist = false)
    private List<String> stationIds;

    /**施工证编号*/
    @ApiModelProperty(value = "施工证编号")
    @Excel(name = "施工证编号", width = 15)
    private String permitCode;

    /**工作证图片*/
    @ApiModelProperty(value = "工作证图片")
    @Excel(name = "工作证图片", width = 15)
    private String cardPics;

    /**工资编号*/
    @ApiModelProperty(value = "工资编号")
    @Excel(name = "工资编号", width = 15)
    private String salaryCode;

}
