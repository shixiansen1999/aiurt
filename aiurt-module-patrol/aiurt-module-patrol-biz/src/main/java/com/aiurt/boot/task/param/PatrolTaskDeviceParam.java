package com.aiurt.boot.task.param;

import com.aiurt.boot.task.entity.PatrolAccompany;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
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
     * 故障单号集合
     */
    @Excel(name = "故障单号集合", width = 15)
    @ApiModelProperty(value = "故障单号集合")
    private List<String> faultList;
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
     * 开始巡检时间起始
     */
    @ApiModelProperty(value = "开始巡检时间起始")
    @Excel(name = "开始巡检时间起始", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTimeBegin;

    /**
     * 开始巡检时间结束
     */
    @ApiModelProperty(value = "开始巡检时间结束")
    @Excel(name = "开始巡检时间结束", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTimeEnd;
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
     * 提交人名称
     */
    @Excel(name = "提交人名称", width = 15)
    @ApiModelProperty(value = "提交人名称")
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
     * 设备位置
     */
    @Excel(name = "设备位置", width = 15)
    @ApiModelProperty(value = "设备位置")
    private String devicePositionName;
    /**
     * 巡检位置
     */
    @Excel(name = "巡检位置", width = 15)
    @ApiModelProperty(value = "巡检位置")
    @TableField(exist = false)
    private String inspectionPositionName;
    /**
     * 巡检时长
     */
    @Excel(name = "巡检时长", width = 15)
    @ApiModelProperty(value = "巡检时长")
    private String duration;
    /**
     * 同行人信息
     */
    @Excel(name = "同行人信息", width = 15)
    @ApiModelProperty(value = "同行人信息")
    private List<PatrolAccompany> accompanyInfo;

    /**
     * 同行人信息字符串
     */
    @Excel(name = "同行人信息字符串", width = 15)
    @ApiModelProperty(value = "同行人信息字符串")
    private String accompanyInfoStr;
    /**
     * 正常项
     */
    @Excel(name = "正常项", width = 15)
    @ApiModelProperty(value = "正常项")
    private Long normalItem;
    /**
     * 异常项
     */
    @Excel(name = "异常项", width = 15)
    @ApiModelProperty(value = "异常项")
    private Long exceptionItem;
}
