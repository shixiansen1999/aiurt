package com.aiurt.modules.weeklyPlan.dto;

import com.aiurt.modules.weeklyPlan.entity.BdLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Lai W.
 * @version 1.0
 */

@Data
@ApiModel(value = "分页查询参数")
public class QueryPagesParams {

    @ApiModelProperty(value = "角色类型id")
    private String roleId;

    @ApiModelProperty(value = "员工id")
    private String staffID;

    @ApiModelProperty(value = "线路")
    private Integer lineID;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private Date beginDate;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;

    @ApiModelProperty(value = "0: 普通周计划查询, 1: 补充计划查询, 2: 审批计划查询")
    private Integer isChange = 0;

    @ApiModelProperty(value = "施工负责人")
    private String chargeStaffId;

    @ApiModelProperty(value = "销点车站")
    private Integer secondStationId;

    @ApiModelProperty(value = "周计划申请状态")
    private Integer formStatus = -1;

    @ApiModelProperty(value = "线路负责人审批状态")
    private Integer lineFormStatus;

    @ApiModelProperty(value = "生产调度审批状态")
    private Integer dispatchFormStatus;

    @ApiModelProperty(value = "主任审批状态")
    private Integer directorFormStatus;

    @ApiModelProperty(value = "经理审批状态")
    private Integer managerFormStatus;

    @ApiModelProperty(value = "供电内容")
    private String powerSupplyRequirement;

    @ApiModelProperty(value = "请点车站id")
    private String firstStationId;

    @ApiModelProperty(value = "作业内容")
    private String taskContent;

    private List<BdLine> bdLineList;

    @ApiModelProperty(value = "管辖班组id集合")
    private List<Integer> teamIdList;

    @ApiModelProperty(value = "周计划申请状态(多状态逗号分割)")
    private String formStatuses;

    @ApiModelProperty(value = "周计划申请状态集合")
    private List<String> formStatusList;
}
