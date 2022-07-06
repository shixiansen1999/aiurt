package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/6
 * @desc
 */
@Data
public class PatrolAccompanySaveDTO {
    /**巡检单号*/
    @Excel(name = "巡检单号", width = 15)
    @ApiModelProperty(value = "巡检单号")
    private java.lang.String patrolNumber;
    /**巡检位置*/
    @Excel(name = "巡检位置", width = 15)
    @ApiModelProperty(value = "巡检位置")
    private java.lang.String position;
    @ApiModelProperty(value = "同行人信息")
    private List<PatrolAccompanyDTO> accompanyDTOList;
}
