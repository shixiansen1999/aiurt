package com.aiurt.boot.plan.dto;

import com.aiurt.boot.plan.entity.RepairPoolCodeContent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wgp
 * @Title: 检修计划详情标准dto
 * @Description:
 * @date 2022/6/239:24
 */
@Data
public class RepairStrategyDTO {
    @ApiModelProperty(value = "检修标准编码")
    private java.lang.String code;

    @ApiModelProperty(value = "检修标准名称")
    private java.lang.String title;

    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    private java.lang.String typeName;

    @ApiModelProperty(value = "适用专业名称")
    private String majorName;

    @ApiModelProperty(value = "适用专业子系统名称")
    private String subsystemName;

    @ApiModelProperty(value = "设备类型")
    private java.lang.String deviceTypeName;

    @ApiModelProperty(value = "是否与设备类型相关")
    private java.lang.String isAppointDeviceTyep;

    @ApiModelProperty(value = "是否指定设备")
    private java.lang.String isAppointDevice;

    @ApiModelProperty(value = "检修项清单（树形）")
    List<RepairPoolCodeContent> repairPoolCodeContentList;

    @ApiModelProperty(value = "设备清单")
    List<RepairDeviceDTO> repairDeviceDTOList;
}
