package org.jeecg.common.system.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 用户角色model
 */
@Data
@ApiModel(value="", description="SysUserRoleModel")
public class SysUserRoleModel {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**用户名称*/
    @Excel(name = "用户名称", width = 15)
    @ApiModelProperty(value = "用户名称")
    private String userName;

    /**角色Id*/
    @Excel(name = "角色Id", width = 15)
    @ApiModelProperty(value = "角色Id")
    private String roleId;

    /**角色编号*/
    @Excel(name = "角色编号", width = 15)
    @ApiModelProperty(value = "角色编号")
    private String roleCode;

    /**角色名称*/
    @Excel(name = "角色名称", width = 15)
    @ApiModelProperty(value = "角色名称")
    private String roleName;
}
