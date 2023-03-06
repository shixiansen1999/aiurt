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
public class PatrolOrgDTO {
    @Excel(name = "身份标识", width = 15)
    @ApiModelProperty(value = "身份标识，传1表示待指派,同行人则不传")
    private Integer identity;
    @Excel(name = "任务Id", width = 15)
    @ApiModelProperty(value = "任务Id")
    private String taskId;
    @Excel(name = "组织机构", width = 15)
    @ApiModelProperty(value = "组织机构")
    private List<String> org;
}

