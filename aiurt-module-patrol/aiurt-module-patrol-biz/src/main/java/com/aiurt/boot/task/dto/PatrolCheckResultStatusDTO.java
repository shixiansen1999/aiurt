package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/8/9
 * @desc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolCheckResultStatusDTO {
    @Excel(name = "已检查数", width = 15)
    @ApiModelProperty(value = "已检查数")
    private java.lang.Integer checkedNumber;
    @Excel(name = "未检查数", width = 15)
    @ApiModelProperty(value = "已检查数")
    private java.lang.Integer unCheckedNumber;

}
