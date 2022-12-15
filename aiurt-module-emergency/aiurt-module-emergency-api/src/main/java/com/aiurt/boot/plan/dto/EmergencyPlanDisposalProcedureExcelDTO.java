package com.aiurt.boot.plan.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class EmergencyPlanDisposalProcedureExcelDTO {

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

	/**应急预案id*/
    @ApiModelProperty(value = "应急预案id")
    private String emergencyPlanId;

	/**处置部门*/
    @ApiModelProperty(value = "处置部门")
    @Dict(dictTable = "sys_depart",dicCode = "org_code",dicText = "depart_name")
    private String orgCode;

    @Excel(name = "处置部门", width = 15)
    @ApiModelProperty(value = "处置部门名称")
    @TableField(exist = false)
    private String orgName;

	/**处置岗位（角色）*/
    @ApiModelProperty(value = "处置岗位（角色）")
    @Dict(dictTable = "sys_role",dicCode = "id",dicText = "role_name")
    private String roleId;

    @Excel(name = "处置岗位（角色）", width = 15)
    @ApiModelProperty(value = "处置岗位（角色）")
    @TableField(exist = false)
    private String roleName;

	/**应急处置内容*/
	@Excel(name = "应急处置内容", width = 15)
    @ApiModelProperty(value = "应急处置内容")
    private String disposalProcedureContent;

}
