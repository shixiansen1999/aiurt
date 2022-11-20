package com.aiurt.modules.subsystem.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.aiurt.modules.basic.entity.DictEntity;
import com.aiurt.modules.system.entity.SysUser;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CsSubsystemDTO extends DictEntity {

    /**所属专业*/
    @Excel(name = "所属专业", width = 15, dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    @ApiModelProperty(value = "所属专业")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    @ExcelExtend(isRequired = true,remark = "必填字段，且在系统中存在该专业；")
    private String majorCode;
    /**所属专业*/
    @ApiModelProperty(value = "所属专业")
    private String majorName;
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**编号*/
    @Excel(name = "子系统编号", width = 15)
    @ApiModelProperty(value = "子系统编号")
    @ExcelExtend(isRequired = true,remark = "子系统编号：必填字段，且不能重复，支持数字、英文字母、符号等；")
    private String systemCode;
	/**名称*/
	@Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    @ExcelExtend(isRequired = true,remark = "子系统名称：必填字段，且不能重复，支持数字、英文字母、符号等；")
    private String systemName;

    /**技术员*/
    @Excel(name = "技术员", width = 15)
    @ApiModelProperty(value = "技术员")
    @TableField(exist = false)
    @ExcelExtend(isRequired = false,remark = "技术员：支持有多个人名，用英文“;”分隔，且在系统用户管理中存在技术员名字；")
    private String systemUserName;

    /**子系统概况*/
    @Excel(name = "子系统概况", width = 15)
    @ApiModelProperty(value = "子系统概况")
    private String generalSituation;

    /**子系统人员*/
    @ApiModelProperty(value = "子系统人员")
    @TableField(exist = false)
    private List<SysUser> systemUserList;

    /**删除标志，0未删除，1已删除*/
    @ApiModelProperty(value = "删除标志，0未删除，1已删除")
    private Integer delFlag;
    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    private String wrongReason;
}
