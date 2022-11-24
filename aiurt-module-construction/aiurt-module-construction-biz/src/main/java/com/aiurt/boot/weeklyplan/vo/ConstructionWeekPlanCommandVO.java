package com.aiurt.boot.weeklyplan.vo;

import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 前端信息渲染对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ConstructionWeekPlanCommandVO对象", description="ConstructionWeekPlanCommandVO对象")
public class ConstructionWeekPlanCommandVO  extends ConstructionWeekPlanCommand {

    private static final long serialVersionUID = -8071884430619763636L;
    /**作业类别字典名*/
    @Excel(name = "作业类别字典名", width = 15)
    @ApiModelProperty(value = "作业类别字典名")
    private String typeDictName;
    /**计划类型字典名*/
    @Excel(name = "计划类型字典名", width = 15)
    @ApiModelProperty(value = "计划类型字典名")
    private String planChangeDictName;
    /**作业单位名称*/
    @Excel(name = "作业单位名称", width = 15)
    @ApiModelProperty(value = "作业单位名称")
    private String orgName;
    /**作业线路名称*/
    @Excel(name = "作业线路名称", width = 15)
    @ApiModelProperty(value = "作业线路名称")
    private String lineName;
    /**施工负责人名称*/
    @Excel(name = "施工负责人名称", width = 15)
    @ApiModelProperty(value = "施工负责人名称")
    private String chargeStaffName;
    /**配合部门名称*/
    @Excel(name = "配合部门名称", width = 15)
    @ApiModelProperty(value = "配合部门名称")
    private String coordinationDepartmentName;
    /**请点车站名称*/
    @Excel(name = "请点车站名称", width = 15)
    @ApiModelProperty(value = "请点车站名称")
    private String firstStationName;
    /**变电所名称*/
    @Excel(name = "变电所名称", width = 15)
    @ApiModelProperty(value = "变电所名称")
    private String substationName;
    /**销点车站名称*/
    @Excel(name = "销点车站名称", width = 15)
    @ApiModelProperty(value = "销点车站名称")
    private String secondStationName;
    /**星期字典名*/
    @Excel(name = "星期字典名", width = 15)
    @ApiModelProperty(value = "星期字典名")
    private String weekdayDictName;
    /**计划令状态字典名*/
    @Excel(name = "计划令状态字典名", width = 15)
    @ApiModelProperty(value = "计划令状态字典名")
    private String formStatusDictName;
    /**申请人名称*/
    @Excel(name = "申请人名称", width = 15)
    @ApiModelProperty(value = "申请人名称")
    private String applyName;
    /**线路负责人名称*/
    @Excel(name = "线路负责人名称", width = 15)
    @ApiModelProperty(value = "线路负责人名称")
    private String lineUserName;
    /**调度人名称*/
    @Excel(name = "调度人名称", width = 15)
    @ApiModelProperty(value = "调度人名称")
    private String dispatchName;
    /**分部主任名称*/
    @Excel(name = "分部主任名称", width = 15)
    @ApiModelProperty(value = "分部主任名称")
    private String directorName;
    /**中心经理名称*/
    @Excel(name = "中心经理名称", width = 15)
    @ApiModelProperty(value = "中心经理名称")
    private String managerName;
    /**生产调度审批状态字典名*/
    @Excel(name = "生产调度审批状态字典名", width = 15)
    @ApiModelProperty(value = "生产调度审批状态字典名")
    private String dispatchStatusDictName;
    /**线路负责人审批状态字典名*/
    @Excel(name = "线路负责人审批状态字典名", width = 15)
    @ApiModelProperty(value = "线路负责人审批状态字典名")
    private String lineStatusDictName;
    /**分部主任审批状态字典名*/
    @Excel(name = "分部主任审批状态字典名", width = 15)
    @ApiModelProperty(value = "计划令状态字典名")
    private String directorStatusDictName;
    /**中心经理审批状态字典名*/
    @Excel(name = "中心经理审批状态字典名", width = 15)
    @ApiModelProperty(value = "中心经理审批状态字典名")
    private String managerStatusDictName;
    /**工区名称*/
    @Excel(name = "工区名称", width = 15)
    @ApiModelProperty(value = "工区名称")
    private String siteName;
    /**作业性质字典名*/
    @Excel(name = "作业性质字典名", width = 15)
    @ApiModelProperty(value = "作业性质字典名")
    private String natureDictName;

    @ApiModelProperty(value = "实例id")
    private String processInstanceId;

}
