package com.aiurt.boot.standard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/6/28
 * @desc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStandardItemsDTO {
    @Excel(name = "标准ID", width = 15)
    @ApiModelProperty(value = "标准ID")
    private String id;
    @Excel(name = "name", width = 15)
    @ApiModelProperty(value = "name")
    private  String name;
    @Excel(name = "pid", width = 15)
    @ApiModelProperty(value = "pid")
    private String pid;
}
