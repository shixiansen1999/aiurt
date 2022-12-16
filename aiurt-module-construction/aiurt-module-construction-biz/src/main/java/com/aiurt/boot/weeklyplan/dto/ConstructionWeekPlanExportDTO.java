package com.aiurt.boot.weeklyplan.dto;

import com.aiurt.boot.constant.ConstructionDictConstant;
import com.aiurt.modules.basic.entity.DictEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 * @description: 施工周计划导出实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConstructionWeekPlanExportDTO extends DictEntity implements Serializable {

    /**
     * 作业性质(1:施工作业、2:巡检作业)
     */
    @Excel(name = "作业性质", dicCode = ConstructionDictConstant.NATURE, orderNum = "1", width = 15)
    private Integer nature;
    /**
     * 作业类别(1:A1、2:A2、3:A3、4:B1、5::C1、6:C2)
     */
    @Excel(name = "作业类别", dicCode = ConstructionDictConstant.CATEGORY, orderNum = "2", width = 15)
    private Integer type;
    /**
     * 作业单位ID
     */
    @Excel(name = "作业单位", dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code", orderNum = "3", width = 15)
    private String orgCode;
    /**
     * 作业日期
     */
    @Excel(name = "作业日期", format = "yyyy-MM-dd", orderNum = "4", width = 15)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date taskDate;
    /**
     * 作业开始时间HH:mm
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date taskStartTime;
    /**
     * 作业结束时间HH:mm
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date taskEndTime;
    /**
     * 格式化作业时间，起始日期比截止时间大的则截止日期加上次日，如00:00-23:00或者23:00-次日00:00
     */
    @Excel(name = "作业时间", orderNum = "5", width = 15)
    private String formatTime;
    /**
     * 作业范围
     */
    @Excel(name = "作业范围", orderNum = "6", width = 15)
    private String taskRange;
    /**
     * 供电要求内容
     */
    @Excel(name = "供电要求", orderNum = "7", width = 15)
    private String powerSupplyRequirementContent;
    /**
     * 作业内容
     */
    @Excel(name = "作业内容", orderNum = "8", width = 15)
    private String taskContent;
    /**
     * 防护措施
     */
    @Excel(name = "防护措施", orderNum = "9", width = 15)
    private String protectiveMeasure;
    /**
     * 施工负责人ID
     */
    @Excel(name = "施工负责人", dictTable = "sys_user", dicText = "realname", dicCode = "id", orderNum = "10", width = 15)
    private String chargeStaffId;
    /**
     * 配合部门编码
     */
    @Excel(name = "配合部门", dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code", orderNum = "11", width = 15)
    private String coordinationDepartmentCode;
    /**
     * 请点车站编码
     */
    @Excel(name = "请点车站", dictTable = "cs_station", dicText = "station_name", dicCode = "station_code", orderNum = "12", width = 15)
    private String firstStationCode;
    /**
     * 销点车站编码
     */
    @Excel(name = "销点车站", dictTable = "cs_station", dicText = "station_name", dicCode = "station_code", orderNum = "13", width = 15)
    private String secondStationCode;
    /**
     * 辅站编码，多个英文逗号分割
     */
    private String assistStationCode;
    /**
     * 辅站名称，多个顿号(、)分割
     */
    @Excel(name = "辅站", orderNum = "14", width = 15)
    private String assistStationName;
    /**
     * 作业人数
     */
    @Excel(name = "作业人数", orderNum = "15", width = 15)
    private Integer taskStaffNum;
    /**
     * 大中型器具
     */
    @Excel(name = "大中型器具", orderNum = "16", width = 15)
    private String largeAppliances;
    /**
     * 星期(1:星期一、2:星期二、3:星期三、4:星期四、5:星期五、6:星期六、7:星期日)
     */
    private Integer weekday;
}
