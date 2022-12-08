package com.aiurt.modules.system.vo;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.modules.system.vo
 * @className: SysUserImportVO
 * @author: life-0
 * @date: 2022/11/1 15:02
 * @description: TODO
 * @version: 1.0
 */
@Data
public class SysUserImportVO {
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
    @ApiModelProperty(value = "部门分配")
    @Excel(name = "部门分配", width = 15)
    private String buName;
    @ApiModelProperty(value = "角色")
    @Excel(name = "角色", width = 15)
    private String names;
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
     * 电话
     */
    @ApiModelProperty(value = "电话")
    @Excel(name = "电话", width = 15)
    private String phone;
    @Excel(name = "错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private java.lang.String text;
}
