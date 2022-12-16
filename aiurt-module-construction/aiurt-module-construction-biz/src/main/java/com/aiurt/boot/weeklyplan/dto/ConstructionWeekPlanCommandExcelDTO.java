package com.aiurt.boot.weeklyplan.dto;

import com.aiurt.boot.constant.ConstructionDictConstant;
import com.aiurt.boot.weeklyplan.entity.ConstructionCommandAssist;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.weeklyplan.dto
 * @className: ConstrctionWeekPlanCommandExcelDTO
 * @author: life-0
 * @date: 2022/12/14 11:56
 * @description: TODO
 * @version: 1.0
 */
@Data
public class ConstructionWeekPlanCommandExcelDTO {
    /**计划令编号*/
    @Excel(name = "计划令编号", width = 15)
    @ApiModelProperty(value = "计划令编号")
    @TableField(value = "`code`")
    private String code;
    /**作业类别(1:A1、2:A2、3:A3、4:B1、5::C1、6:C2)*/
    @ApiModelProperty(value = "作业类别(1:A1、2:A2、3:A3、4:B1、5::C1、6:C2)")
    @Dict(dicCode = ConstructionDictConstant.CATEGORY)
    private Integer type;
    @Excel(name = "作业类别", width = 15)
    private String typeName;
    /**计划类型(1正常计划 2日补充计划 3临时补修计划*/
    @Excel(name = "计划类型(1正常计划 2日补充计划 3临时补修计划", width = 15)
    @ApiModelProperty(value = "计划类型(1正常计划 2日补充计划 3临时补修计划")
    @Dict(dicCode = ConstructionDictConstant.PLAN_TYPE)
    private Integer planChange;
    /**作业单位ID*/
    @ApiModelProperty(value = "作业单位ID")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    private String orgCode;
    @Excel(name = "作业单位", width = 15)
    private String orgName;
    /**作业日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "作业日期")
    private Date taskDate;
    @Excel(name = "作业日期", width = 15)
    private String date;
    /**作业开始时间HH:mm*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "作业开始时间HH:mm")
    private Date taskStartTime;
    /**作业结束时间HH:mm*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "作业结束时间HH:mm")
    private Date taskEndTime;
    @Excel(name = "作业时间")
    private String endAndTime;
    /**作业范围*/
    @Excel(name = "线路作业范围", width = 15)
    @ApiModelProperty(value = "作业范围")
    private String taskRange;
    /**作业线路编码*/
    @Excel(name = "作业线路编码", width = 15)
    @ApiModelProperty(value = "作业线路编码")
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    private String lineCode;
    @Excel(name = "作业线路", width = 15)
    private String lineName;
    /**供电要求ID*/
    @Excel(name = "供电要求ID", width = 15)
    @ApiModelProperty(value = "供电要求ID")
    private String powerSupplyRequirementId;
    /**供电要求内容*/
    @Excel(name = "供电要求内容", width = 15)
    @ApiModelProperty(value = "供电要求内容")
    private String powerSupplyRequirementContent;
    /**作业内容*/
    @Excel(name = "作业内容", width = 15)
    @ApiModelProperty(value = "作业内容")
    private String taskContent;
    /**防护措施*/
    @Excel(name = "防护措施", width = 15)
    @ApiModelProperty(value = "防护措施")
    private String protectiveMeasure;
    /**施工负责人ID*/
    @Excel(name = "施工负责人ID", width = 15)
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @ApiModelProperty(value = "施工负责人ID")
    private String chargeStaffId;
    @Excel(name = "施工负责人", width = 15)
    private String chargeStaffName;
    /**配合部门编码*/
    @Excel(name = "配合部门编码", width = 15)
    @ApiModelProperty(value = "配合部门编码")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    private String coordinationDepartmentCode;
    @Excel(name = "配合部门", width = 15)
    private String coordinationDepartmentName;
    /**请点车站编码*/
    @Excel(name = "请点车站编码", width = 15)
    @ApiModelProperty(value = "请点车站编码")
    @Dict(dictTable = "cs_station", dicText = "station_name", dicCode = "station_code")
    private String firstStationCode;
    @Excel(name = "请点车站", width = 15)
    private String firstStationName;
    /**变电所编码*/
    @Excel(name = "变电所编码", width = 15)
    @ApiModelProperty(value = "变电所编码")
    @Dict(dictTable = "cs_station", dicText = "station_name", dicCode = "station_code")
    private String substationCode;
    /**销点车站编码*/
    @Excel(name = "销点车站编码", width = 15)
    @ApiModelProperty(value = "销点车站编码")
    @Dict(dictTable = "cs_station", dicText = "station_name", dicCode = "station_code")
    private String secondStationCode;
    @Excel(name = "销点车站", width = 15)
    private String secondStationName;
    /**作业人数*/
    @ApiModelProperty(value = "作业人数")
    private Integer taskStaffNum;
    @Excel(name = "作业人数", width = 15)
    private String num;
    /**大中型器具*/
    @Excel(name = "大中型器具", width = 15)
    @ApiModelProperty(value = "大中型器具")
    private String largeAppliances;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**星期(1:星期一、2:星期二、3:星期三、4:星期四、5:星期五、6:星期六、7:星期日)*/
    @Excel(name = "星期(1:星期一、2:星期二、3:星期三、4:星期四、5:星期五、6:星期六、7:星期日)", width = 15)
    @ApiModelProperty(value = "星期(1:星期一、2:星期二、3:星期三、4:星期四、5:星期五、6:星期六、7:星期日)")
    @Dict(dicCode = ConstructionDictConstant.WEEK)
    private Integer weekday;
    /**计划令状态(0待提审、1待审核、2审核中、3已驳回、4已取消、5已通过)*/
    @Excel(name = "计划令状态(0待提审、1待审核、2审核中、3已驳回、4已取消、5已通过)", width = 15)
    @ApiModelProperty(value = "计划令状态(0待提审、1待审核、2审核中、3已驳回、4已取消、5已通过)")
    @Dict(dicCode =ConstructionDictConstant.STATUS)
    private Integer formStatus;
    /**申请人ID*/
    @Excel(name = "申请人ID", width = 15)
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    @ApiModelProperty(value = "申请人ID")
    private String applyId;
    /**线路负责人ID*/
    @Excel(name = "线路负责人ID", width = 15)
    @ApiModelProperty(value = "线路负责人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private String lineUserId;
    /**调度人ID*/
    @Excel(name = "调度人ID", width = 15)
    @ApiModelProperty(value = "调度人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private String dispatchId;
    /**分部主任ID*/
    @Excel(name = "分部主任ID", width = 15)
    @ApiModelProperty(value = "分部主任ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private String directorId;
    /**中心经理ID*/
    @Excel(name = "中心经理ID", width = 15)
    @ApiModelProperty(value = "中心经理ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private String managerId;

    @ApiModelProperty(value = "工区编码")
    private String siteCode;
    /**作业性质(1:施工作业、2:巡检作业)*/
    @Excel(name = "作业性质(1:施工作业、2:巡检作业)", width = 15)
    @ApiModelProperty(value = "作业性质(1:施工作业、2:巡检作业)")
    @Dict(dicCode = ConstructionDictConstant.NATURE)
    private Integer nature;
    @Excel(name = "作业性质", width = 15)
    private String natureName;
    /**辅站信息*/
    @ApiModelProperty(value = "辅站信息")
    @TableField(exist = false)
    private List<ConstructionCommandAssist> constructionAssist;
    @Excel(name = "辅站", width = 15)
    private String construction;
    @Excel(name = "错误原因", width = 15)
    private String text;


}
