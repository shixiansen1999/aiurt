package com.aiurt.modules.param.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description: sys_param
 * @Author: aiurt
 * @Date: 2022-12-15
 * @Version: V1.0
 */
@Data
@TableName("sys_param")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sys_param对象", description = "sys_param")
public class SysParam extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 新增保存时的校验分组
     */
    public interface Save {
    }

    /**
     * 修改时的校验分组
     */
    public interface Update {
    }

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @NotBlank(message = "主键ID不能为空！", groups = Update.class)
    private java.lang.String id;
    /**
     * 参数编号
     */
    @Excel(name = "参数编号", width = 15)
    @ApiModelProperty(value = "参数编号")
    @NotBlank(message = "参数编号不能为空！", groups = {Save.class, Update.class})
    @TableField(value = "`code`")
    private java.lang.String code;
    /**
     * 参数类别
     */
    @Excel(name = "参数类别", width = 15)
    @ApiModelProperty(value = "参数类别")
    @Dict(dicCode = "param_category")
    private java.lang.Integer category;
    /**
     * 参数值
     */
    @Excel(name = "参数值", width = 15)
    @ApiModelProperty(value = "参数值")
    @TableField(value = "`value`")
    private java.lang.String value;
    /**
     * 参数说明
     */
    @Excel(name = "参数说明", width = 15)
    @ApiModelProperty(value = "参数说明")
    @TableField(value = "`explain`")
    private java.lang.String explain;
    /**
     * 登录页logo参数
     */
    @Excel(name = "登录页logo参数", width = 15)
    @ApiModelProperty(value = "登录页logo参数")
    private java.lang.String loginLogo;
    /**
     * 登录页title
     */
    @Excel(name = "登录页title参数", width = 15)
    @ApiModelProperty(value = "登录页title参数")
    private java.lang.String loginTitle;
    /**
     * 主页logo参数
     */
    @Excel(name = "主页logo参数", width = 15)
    @ApiModelProperty(value = "主页logo参数")
    private java.lang.String homeLogo;
    /**
     * 主页logo附近的title参数
     */
    @Excel(name = "主页logo附近的title参数", width = 15)
    @ApiModelProperty(value = "主页logo附近的title参数")
    private java.lang.String homeLeftTitle;
    /**
     * 主页右边的title参数
     */
    @Excel(name = "主页右边的title参数", width = 15)
    @ApiModelProperty(value = "主页右边的title参数")
    private java.lang.String homeRightTitle;
    /**
     * 是否当前系统应用参数 0否，1是
     */
    @Excel(name = "是否当前系统应用参数", width = 15)
    @ApiModelProperty(value = "是否当前系统应用参数 0否，1是")
    @Dict(dicCode = "sys_system_apps")
    private java.lang.Integer isSystemApps;
    /**
     * 删除状态： 0未删除 1已删除
     */
    @Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
