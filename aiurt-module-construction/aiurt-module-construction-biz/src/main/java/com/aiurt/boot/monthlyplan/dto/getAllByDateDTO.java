package com.aiurt.boot.monthlyplan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: TODO
 * @author: Sand Sculpture King
 * @date: 2021年05月25日 14:36
 */
@Data
@ApiModel(value = "通过日期查询月计划数据")
public class getAllByDateDTO implements Serializable {

    String id;
    String type;
    String remark;
    String weekDay;
    String applyStaffId;
    String rejectedReason;
    @Excel(name = "taskDate", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "taskDate")
    Date taskDate;
    String taskTime;
    String taskRange;
    String formStatus;
    String planChange;
    String taskContent;
    String departmentId;
    String chargeStaffId;
    String firstStationId;
    String lineFormStatus;
    String secondStationId;
    String largeAppliances;
    String assistStationIds;
    String protectiveMeasure;
    String dispatchFormStatus;
    String powerSupplyRequirement;
    String assistStationManagerIds;
    String coordinationDepartmentid;


}
