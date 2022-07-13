package com.aiurt.boot.manager.dto;

import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.manager.dto
 * @className: InspectionCodeDTO
 * @author: life-0
 * @date: 2022/6/29 12:11
 * @description: TODO
 * @version: 1.0
 */
@Data
public class InspectionCodeDTO extends InspectionCode {
    /**设备类型名称*/
    @Excel(name = "设备类型名称", width = 15)
    @ApiModelProperty(value = "设备类型名称")
    private java.lang.String deviceTypeName;
    /**专业名称*/
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private java.lang.String majorName;
    @Excel(name = "专业子系统名称", width = 15)
    @ApiModelProperty(value = "专业子系统名称")
    private java.lang.String subsystemName;
    @Excel(name = "判断是否可以删除0为可删,其他都为不可删",width = 15)
    @ApiModelProperty(value = "判断是否可以删除0为可删,其他都为不可删")
    private Integer number;
    @Excel(name = "巡检策略Code")
    @ApiModelProperty(value = "巡检策略Code")
    private String inspectionStrCode;
    @ApiModelProperty(value = "选择的设备集合")
    @TableField(exist = false)
    List<Device> devices;
}
