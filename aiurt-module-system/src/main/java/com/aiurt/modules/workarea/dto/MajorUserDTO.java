package com.aiurt.modules.workarea.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/8/11
 * @desc
 */
@Data
public class MajorUserDTO {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    @Excel(name = "用户账号", width = 15)
    private String username;
    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    @Excel(name = "真实姓名", width = 15)
    private String realname;
    /**
     * 性别（1：男 2：女）
     */
    @ApiModelProperty(value = "性别（1：男 2：女）")
    @Excel(name = "性别", width = 15,dicCode="sex")
    @Dict(dicCode = "sex")
    private Integer sex;
    /**部门名称*/
    @ApiModelProperty(value = " 所属部门/部门名称")
    private transient String orgName;
    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    @Excel(name = "电话", width = 15)
    private String phone;
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private String majorNames;
    @ApiModelProperty(value = "子系统名称")
    @TableField(exist = false)
    private String systemNames;

    @Excel(name = "专业code", width = 15)
    @ApiModelProperty(value = "专业code")
    private List<String> majorCodeList;
}
