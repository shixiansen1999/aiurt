package com.aiurt.boot.strategy.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Description
 * @Author MrWei
 * @Date 2022/11/22 12:12
 **/
@Data
public class DeviceExcelDTO {
    /**
     * 设备编号
     */
    @Excel(name = "设备编号", width = 25)
    @ApiModelProperty(value = "设备编号")
    private String code;
    /**
     * 设备名称
     */
    @Excel(name = "设备名称", width = 25)
    @ApiModelProperty(value = "设备名称")
    private String name;


}
