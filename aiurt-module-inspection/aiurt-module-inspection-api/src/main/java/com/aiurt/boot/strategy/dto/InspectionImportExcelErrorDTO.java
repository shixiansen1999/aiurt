package com.aiurt.boot.strategy.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author MrWei
 * @Date 2022/11/22 12:12
 **/
@Data
public class InspectionImportExcelErrorDTO {
    @ApiModelProperty(value = "检修计划策略标准关联表Id")
    private String Id;
    /**
     * 检修标准编码
     */
    @Excel(name = "检修标准编码", width = 20)
    @ApiModelProperty(value = "检修标准编码")
    private String code;
    /**
     * 检修标准名称
     */
    @Excel(name = "检修标准名称", width = 25)
    @ApiModelProperty(value = "检修标准名称")
    private String title;

    @Excel(name = "所选设备", width = 25)
    @ApiModelProperty(value = "所选设备")
    private String deviceExcelDTOS;

    /**
     * 检修标准错误原因
     */
    @Excel(name = "检修标准错误原因", width = 15)
    @ApiModelProperty(value = "检修标准错误原因")
    @TableField(exist = false)
    private String errorReason;
}
