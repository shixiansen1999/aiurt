package com.aiurt.boot.rehearsal.vo;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author
 * @date 2022/12/1 15:16
 * @description: 月计划条件分页查询列表VO对象
 */
@Data
public class EmergencyRehearsalMonthVO extends EmergencyRehearsalMonth {
    /**
     * 年度,格式yyyy
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy")
    @DateTimeFormat(pattern = "yyyy")
    @ApiModelProperty(value = "年度,格式yyyy")
    private Date year;
}
