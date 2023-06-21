package com.aiurt.boot.rehearsal.vo;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author
 * @date 2022/12/1 15:16
 * @description: 月计划条件分页查询列表VO对象
 */
@Data
@ApiModel(value = "月计划条件分页查询列表VO对象", description = "月计划条件分页查询列表VO对象")
public class EmergencyRehearsalMonthVO extends EmergencyRehearsalMonth {
    /**
     * 年度
     */
    @ApiModelProperty(value = "年度")
    private String year;
    /**
     * 是否能删除标识
     */
    @ApiModelProperty(value = "是否能删除标识")
    private Boolean delete;
    /**
     * 应急年度演练计划的计划编号
     */
    @ApiModelProperty(value = "应急年度演练计划的计划编号")
    private String emergencyRehearsalYearCode;
    /**
     * 应急年度演练计划的计划名称
     */
    @ApiModelProperty(value = "应急年度演练计划的计划名称")
    private String emergencyRehearsalYearName;
}
