package com.aiurt.modules.weeklyPlan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: 周计划令变更记录表
 * @author: Lai W.
 * @version: 1.0
 * @date 2021-06-07
 */

@Data
@TableName("bd_operate_plan_state_change")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_operate_plan_state_change对象", description = "周计划变更计划表")
public class BdOperatePlanStateChange implements Serializable {
    private static final long serialVisionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "计划令变更计划表")
    private String id;

    @ApiModelProperty(value = "对应计划令id")
    private String bdOperatePlanDeclarationFormId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "变更操作人id")
    private String changeStaffId;

    @ApiModelProperty(value = "变更操作原因（驳回原因等）")
    private String changeReason;

    @ApiModelProperty(value = "线路负责人原先审批的状态，对应bd_operate_plan_declaration_form表中的line_form_status原先状态")
    private Integer forwardLineStatus;

    @ApiModelProperty(value = "生产调度原先审批的状态，对应bd_operate_plan_declaration_form表中的dispatch_form_status原先状态")
    private Integer forwardDispatchStatus;

    @ApiModelProperty(value = "经理原先审批的状态，对应bd_operate_plan_declaration_form表中的manager_form_status原先状态")
    private Integer forwardManagerStatus;

    @ApiModelProperty(value = "主任原先审批的状态，对应bd_operate_plan_declaration_form表中的director_form_status原先状态")
    private Integer forwardDirectorStatus;

    @ApiModelProperty(value = "变更操作后状态，对应bd_operate_plan_declaration_form表中的form_status修改后状态")
    private Integer afterStatus;

    @ApiModelProperty(value = "变更人当前角色id")
    private String roleId;

}
