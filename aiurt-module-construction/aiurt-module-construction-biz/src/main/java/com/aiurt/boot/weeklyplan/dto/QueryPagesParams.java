package com.aiurt.boot.weeklyplan.dto;

import com.aiurt.modules.position.entity.CsLine;
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
    /**
     * 角色类型id
     */
    @ApiModelProperty(value = "角色类型id")
    private String roleId;
    /**
     * 员工id
     */
    @ApiModelProperty(value = "员工id")
    private String staffID;
    /**
     * 线路
     */
    @ApiModelProperty(value = "线路")
    private String lineID;
    /**
     * 开始日期yyyy-MM-dd
     */
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private Date beginDate;
    /**
     * 结束日期yyyy-MM-dd
     */
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;
    /**
     * 0: 普通周计划查询, 1: 补充计划查询, 2: 审批计划查询
     */
    @ApiModelProperty(value = "0: 普通周计划查询, 1: 补充计划查询, 2: 审批计划查询")
    private Integer isChange = 0;
    /**
     * 施工负责人
     */
    @ApiModelProperty(value = "施工负责人")
    private String chargeStaffId;
    /**
     * 销点车站
     */
    @ApiModelProperty(value = "销点车站")
    private String secondStationId;
    /**
     * 周计划申请状态
     */
    @ApiModelProperty(value = "周计划申请状态")
    private Integer formStatus = -1;
    /**
     * 线路负责人审批状态
     */
    @ApiModelProperty(value = "线路负责人审批状态")
    private Integer lineFormStatus;
    /**
     * 生产调度审批状态
     */
    @ApiModelProperty(value = "生产调度审批状态")
    private Integer dispatchFormStatus;
    /**
     * 主任审批状态
     */
    @ApiModelProperty(value = "主任审批状态")
    private Integer directorFormStatus;
    /**
     * 经理审批状态
     */
    @ApiModelProperty(value = "经理审批状态")
    private Integer managerFormStatus;
    /**
     * 供电内容
     */
    @ApiModelProperty(value = "供电内容")
    private String powerSupplyRequirement;
    /**
     * 请点车站id
     */
    @ApiModelProperty(value = "请点车站id")
    private String firstStationId;
    /**
     * 作业内容
     */
    @ApiModelProperty(value = "作业内容")
    private String taskContent;

    private List<CsLine> bdLineList;
    /**
     * 管辖班组id集合
     */
    @ApiModelProperty(value = "管辖班组id集合")
    private List<String> teamIdList;
    /**
     * 周计划申请状态(多状态逗号分割)
     */
    @ApiModelProperty(value = "周计划申请状态(多状态逗号分割)")
    private String formStatuses;
    /**
     * 周计划申请状态集合
     */
    @ApiModelProperty(value = "周计划申请状态集合")
    private List<String> formStatusList;
}
