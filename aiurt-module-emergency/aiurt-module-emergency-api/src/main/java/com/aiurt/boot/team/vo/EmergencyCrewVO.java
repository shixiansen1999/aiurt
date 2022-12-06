package com.aiurt.boot.team.vo;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author lkj
 */
@Data
public class EmergencyCrewVO {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**职务*/
    @Excel(name = "职务", width = 15)
    @ApiModelProperty(value = "职务")
    @Dict(dicCode = "emergency_post")
    private Integer post;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    @Excel(name = "真实姓名", width = 15)
    private String realname;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    @Excel(name = "电话", width = 15)
    private String phone;

    @ApiModelProperty(value = "角色名")
    private String roleNames;

    /**
     * 部门name(当前选择登录部门)
     */
    @ApiModelProperty(value = " 部门name(所属部门)")
    private String orgName;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;

    /**应急队伍训练记录参训人员关联表id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "应急队伍训练记录参训人员关联表id")
    private String recordCrewId;
}
