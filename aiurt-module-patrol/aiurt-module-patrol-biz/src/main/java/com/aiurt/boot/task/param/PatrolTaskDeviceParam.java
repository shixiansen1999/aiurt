package com.aiurt.boot.task.param;

import com.aiurt.boot.task.entity.PatrolAccompany;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PatrolTaskDeviceParam extends PatrolTaskDevice {

    /**
     * 专业编码
     */
    @Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    private String majorCode;

    /**
     * 专业名称
     */
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private String majorName;
    /**
     * 子系统编码
     */
    @Excel(name = "子系统编码", width = 15)
    @ApiModelProperty(value = "子系统编码")
    private String subsystemCode;
    /**
     * 子系统名称
     */
    @Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    private String subsystemName;
    /**
     * 设备类型编码
     */
    @Excel(name = "设备类型编码", width = 15)
    @ApiModelProperty(value = "设备类型编码")
    private String deviceTypeCode;
    /**
     * 设备类型名称
     */
    @Excel(name = "设备类型名称", width = 15)
    @ApiModelProperty(value = "设备类型名称")
    private String deviceTypeName;
    /**
     * 标准名称
     */
    @Excel(name = "标准名称", width = 15)
    @ApiModelProperty(value = "标准名称")
    private String standardName;
    /**
     * 设备名称
     */
    @Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private String deviceName;
    /**
     * 巡检人名称
     */
    @Excel(name = "巡检人名称", width = 15)
    @ApiModelProperty(value = "巡检人名称")
    private String username;
    /**
     * 线路名称
     */
    @Excel(name = "线路名称", width = 15)
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    /**
     * 站点名称
     */
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    /**
     * 位置名称
     */
    @Excel(name = "位置名称", width = 15)
    @ApiModelProperty(value = "位置名称")
    private String positionName;
    /**
     * 巡检时长，单位分钟
     */
    @Excel(name = "巡检时长，单位分钟", width = 15)
    @ApiModelProperty(value = "巡检时长，单位分钟")
    private Long duration;
    /**
     * 同行人信息
     */
    @Excel(name = "巡检时长", width = 15)
    @ApiModelProperty(value = "巡检时长")
    private List<PatrolAccompany> accompanyInfo;
}
