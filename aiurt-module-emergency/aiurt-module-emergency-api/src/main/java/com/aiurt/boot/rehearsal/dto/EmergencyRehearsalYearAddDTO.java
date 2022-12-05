package com.aiurt.boot.rehearsal.dto;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @author
 * @date 2022/11/29 10:54
 * @description: 年演练计划添加操作的DTO对象
 */
@Data
public class EmergencyRehearsalYearAddDTO extends EmergencyRehearsalYear {
    /**
     * 月演练计划列表
     */
    @ApiModelProperty(value = "月演练计划列表")
    @Valid
    private List<EmergencyRehearsalMonth> monthList;
}
