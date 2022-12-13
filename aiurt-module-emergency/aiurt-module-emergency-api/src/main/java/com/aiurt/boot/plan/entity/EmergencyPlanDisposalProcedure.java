package com.aiurt.boot.plan.entity;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description: emergency_plan_disposal_procedure
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_plan_disposal_procedure")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_plan_disposal_procedure对象", description="emergency_plan_disposal_procedure")
public class EmergencyPlanDisposalProcedure extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 新增保存时的校验分组
     */
    public interface Save {}

    /**
     * 修改时的校验分组
     */
    public interface Update {}
	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**应急预案id*/
	@Excel(name = "应急预案id", width = 15)
    @ApiModelProperty(value = "应急预案id")
    private String emergencyPlanId;
	/**处置部门*/
	@Excel(name = "处置部门", width = 15)
    @ApiModelProperty(value = "处置部门")
    @NotBlank(message = "处置部门不能为空",groups = {Save.class,Update.class})
    @Dict(dictTable = "sys_depart",dicCode = "org_code",dicText = "depart_name")
    private String orgCode;

    @ApiModelProperty(value = "处置部门名称")
    @TableField(exist = false)
    private String orgName;

	/**处置岗位（角色）*/
	@Excel(name = "处置岗位（角色）", width = 15)
    @ApiModelProperty(value = "处置岗位（角色）")
    @NotBlank(message = "处置岗位不能为空",groups = {Save.class,Update.class})
    @Dict(dictTable = "sys_role",dicCode = "id",dicText = "role_name")
    private String roleId;

    @ApiModelProperty(value = "处置岗位（角色）")
    @TableField(exist = false)
    private String roleName;

	/**应急处置内容*/
	@Excel(name = "应急处置内容", width = 15)
    @ApiModelProperty(value = "应急处置内容")
    @NotBlank(message = "处置内容不能为空",groups = {Save.class,Update.class})
    private String disposalProcedureContent;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
