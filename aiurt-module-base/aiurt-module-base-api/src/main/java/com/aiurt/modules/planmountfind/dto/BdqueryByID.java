package com.aiurt.modules.planmountfind.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(value = "通过id查询接口")
public class BdqueryByID {

    String id;
    String type;
    String remark;
    String weekDay;
    String code;
    String picture;
    String rejectedReason;
    @Excel(name = "taskDate", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "taskDate")
    Date taskDate;
    String taskTime;
    String taskRange;
    String formStatus;
    String planChange;
    String taskContent;
    String applyStaffId;
    String taskStaffNum;
    String departmentId;
    String chargeStaffId;
    String firstStationId;
    String lineFormStatus;
    String secondStationId;
    String largeAppliances;
    String assistStationName;
    String protectiveMeasure;
    String dispatchFormStatus;
    String powerSupplyRequirement;
    String assistStationManagerIds;
    String coordinationDepartmentid;

    String lineStaffID;
    String dispatchStaffID;
}
