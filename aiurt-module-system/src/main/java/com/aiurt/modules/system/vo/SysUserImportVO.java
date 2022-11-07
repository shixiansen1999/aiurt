package com.aiurt.modules.system.vo;

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
}
