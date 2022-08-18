package com.aiurt.boot.standard.dto;

import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.standard.dto
 * @className: PatrolStandardDto
 * @author: life-0
 * @date: 2022/6/23 17:33
 * @description: TODO
 * @version: 1.0
 */
@Data
public class PatrolStandardDto extends PatrolStandard {
    @Excel(name = "适用系统名称", width = 15)
    @ApiModelProperty(value = "适用系统名称")
    @TableField(exist = false)
    private java.lang.String subsystemName;
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private java.lang.String professionName;
    @Excel(name = "设备类型名称", width = 15)
    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private java.lang.String deviceTypeName;
    @Excel(name = "判断是否可以删除0为可删,其他都为不可删",width = 15)
    @ApiModelProperty(value = "判断是否可以删除0为可删,其他都为不可删")
    private Integer number;
    @Excel(name = "选择站点集合",width = 15)
    @ApiModelProperty(value = "选择站点集合")
    private List<String> stations;
    @TableField(exist = false)
    @Excel(name = "设备集合", width = 15)
    @ApiModelProperty(value = "设备集合")
    List<Device> devicesSs;
}
