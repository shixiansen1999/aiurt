package com.aiurt.boot.weeklyplan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhn
 * @version 1.0
 */

@Data
public class ProductPlanDTO {

    @ApiModelProperty(value = "序号")
    private Integer number;

    @ApiModelProperty(value = "原生产计划表id(包括周计划表的id、月计划表的id)")
    private Integer id;

    @ApiModelProperty(value = "作业类别")
    private String type;

    @ApiModelProperty(value = "作业单位")
    private String departmentName;

    @ApiModelProperty(value = "创建时间")
    private String dateTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "作业日期")
    private Date taskDate;

    @ApiModelProperty(value = "作业时间")
    private String taskTime;

    @ApiModelProperty(value = "作业范围")
    private String taskRange;

    @ApiModelProperty(value = "供电要求")
    private String powerRequirement;

    @ApiModelProperty(value = "作业内容")
    private String taskContent;

    @ApiModelProperty(value = "防护措施")
    private String protectiveMeasure;

    @ApiModelProperty(value = "施工负责人id")
    private String chargeStaffId;

    @ApiModelProperty(value = "施工负责人")
    private String chargeStaffName;

    @ApiModelProperty(value = "配合部门")
    private String coordinationDepartment;

    @ApiModelProperty(value = "请点车站id")
    private String firstStationId;

    @ApiModelProperty(value = "请点车站")
    private String firstStationName;

    @ApiModelProperty(value = "销点车站id")
    private String secondStationId;

    @ApiModelProperty(value = "销点车站")
    private String secondStationName;

    @ApiModelProperty(value = "变电所id")
    private String subStationId;

    @ApiModelProperty(value = "变电所")
    private String subStationName;

    @ApiModelProperty(value = "辅站id")
    private String assistStationId;

    @ApiModelProperty(value = "辅站")
    private String assistStationName;

    @Excel(name = "作业人数")
    @ApiModelProperty(value = "作业人数")
    private Integer taskStaffNum;

    @Excel(name = "大中型器具")
    @ApiModelProperty(value = "大中型器具")
    private String largeAppliances;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "星期一到星期日")
    private Integer weekday;

    @ApiModelProperty(value = "计划令图片")
    private String picture;

    @ApiModelProperty(value = "计划令录音")
    private String voice;

    @ApiModelProperty(value = "状态：0申请中 1 同意 2驳回 3草稿保存 4 已取消")
    private Integer formStatus;

    @ApiModelProperty(value = "申请人id")
    private String applyStaffId;

    @ApiModelProperty(value = "申请人")
    private String applyStaffName;

    @ApiModelProperty(value = "线路负责人id")
    private String lineStaffId;

    @ApiModelProperty(value = "线路负责人")
    private String lineStaffName;

    @ApiModelProperty(value = "调度人id")
    private String dispatchStaffId;

    @ApiModelProperty(value = "调度人")
    private String dispatchStaffName;

    @ApiModelProperty(value = "生产调度审批状态: 0 未审批 1 通过 2 驳回")
    private Integer dispatchFormStatus;

    @ApiModelProperty(value = "线路负责人审批状态 0 未审批 1 通过 2 驳回")
    private Integer lineFormStatus;

    @ApiModelProperty(value = "辅站负责人")
    private String assistStationManagerNames;

    @ApiModelProperty(value = "0正常计划 1计划补修 2日计划补充 3施工变更 4 施工取消")
    private Integer planChange;

    @ApiModelProperty(value = "变更关联的已通过计划令")
    private Integer changeCorrelation;

    @Excel(name = "作业性质", width = 10)
    @ApiModelProperty(value = "作业性质")
    private String nature;

    @ApiModelProperty(value = "工区id")
    private Integer siteId;
    @ApiModelProperty(value = "工区名称")
    private String siteName;

    @TableField(exist = false)
    @ApiModelProperty(value = "线路id")
    private String lineId;

    @TableField(exist = false)
    @ApiModelProperty(value = "任务日期-开始")
    private String beginDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "任务日期-结束")
    private String endDate;


}
