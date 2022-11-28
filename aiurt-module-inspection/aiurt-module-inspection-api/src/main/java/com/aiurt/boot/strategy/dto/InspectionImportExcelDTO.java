package com.aiurt.boot.strategy.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.util.List;

/**
 * @Description
 * @Author MrWei
 * @Date 2022/11/22 12:12
 **/
@Data
public class InspectionImportExcelDTO {
    @ApiModelProperty(value = "检修计划策略标准关联表Id")
    private java.lang.String Id;
    /**
     * 检修标准编码
     */
    @Excel(name = "检修标准编码", width = 20)
    @ApiModelProperty(value = "检修标准编码")
    private java.lang.String code;
    /**
     * 检修标准名称
     */
    @Excel(name = "检修标准名称", width = 25)
    @ApiModelProperty(value = "检修标准名称")
    private java.lang.String title;

    @Excel(name = "所选设备编码", width = 25)
    @ApiModelProperty(value = "所选设备编码")
    private String deviceExcelDTOS;

    /**
     * 检修标准错误原因
     */
    @ApiModelProperty(value = "检修标准错误原因")
    @TableField(exist = false)
    private String errorReason;
}
