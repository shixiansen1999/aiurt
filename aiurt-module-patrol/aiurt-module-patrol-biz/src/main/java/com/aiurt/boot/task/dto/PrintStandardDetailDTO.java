package com.aiurt.boot.task.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @author qkx
 */
@Data
public class PrintStandardDetailDTO {
    /**
     * 巡检单ID
     */
    @ApiModelProperty(value = "巡检单ID")
    private String billId;
    /**
     * 巡检单编号
     */
    @ApiModelProperty(value = "巡检单编号")
    private String billCode;
    /**
     * 工单表名
     */
    @ApiModelProperty(value = "工单表名")
    private String tableName;
    /**
     * 站点编码
     */
    @ApiModelProperty(value = "站点编码")
    private String stationCode;
    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String stationName;

    @ApiModelProperty(value = "程序和方法")
    private String procMethods;

    @ApiModelProperty(value = "巡视人")
    private String userName;

    @ApiModelProperty(value = "抽检人")
    private String spotCheckUserName;

    @ApiModelProperty(value = "提交时间")
    private String submitTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "抽检时间")
    private java.util.Date spotCheckTime;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备位置")
    private String deviceLocation;

    @ApiModelProperty(value = "巡检频次")
    @Dict(dicCode = "patrol_plan_period")
    private java.lang.Integer period;

    @ApiModelProperty(value = "任务提交的用户签名图片")
    private String signUrl;

    @ApiModelProperty(value = "抽检人的用户签名图片")
    private String sampleSignUrl;
    /**
     * 巡检项目树
     */
    @ApiModelProperty(value = "巡检项目树")
    private List<PatrolCheckResultDTO> children;

    /**
     * 组织机构信息
     */
    @Excel(name = "组织机构信息", width = 15)
    @ApiModelProperty(value = "组织机构信息")
    private List<PatrolTaskOrganizationDTO> departInfo;

    private String id ;
}
