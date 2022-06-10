package com.aiurt.boot.modules.fault.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
@Data
public class FaultDTO{

    /**主键id,自动递增*/
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id,自动递增")
    private Long id;

    /**故障编号，示例：G101.2109.001*/
    @Excel(name = "故障编号，示例：G101.2109.001", width = 15)
    @ApiModelProperty(value = "故障编号，示例：G101.2109.001")
    private String code;

    /**线路编号*/
    @Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
    @NotBlank(message = "线路编号不能为空")
    private String lineCode;

    /**站点编号*/
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    @NotBlank(message = "站点编号不能为空")
    private String stationCode;

    /**故障设备编号集合*/
    @Excel(name = "故障设备编号集合", width = 15)
    @ApiModelProperty(value = "故障设备编号集合")
    private String devicesIds;

    /**故障现象*/
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    @NotBlank(message = "故障现象不能为空")
    private String faultPhenomenon;

    /**故障类型*/
    @Excel(name = "故障类型", width = 15)
    @ApiModelProperty(value = "故障类型")
    @NotNull(message = "故障类型不能为空")
    private Integer faultType;

    /**状态：0-新报修 1-维修中 2-维修完成*/
    @Excel(name = "状态：0-新报修 1-维修中 2-维修完成", width = 15)
    @ApiModelProperty(value = "状态：0-新报修 1-维修中 2-维修完成")
    private Integer status;

    /**指派状态：0-未指派 1-指派 2-重新指派*/
    @Excel(name = "指派状态：0-未指派 1-指派 2-重新指派", width = 15)
    @ApiModelProperty(value = "指派状态：0-未指派 1-指派 2-重新指派")
    private Integer assignStatus;

    /**故障级别：1-普通故障 2-重大故障*/
    @Excel(name = "故障级别：1-普通故障 2-重大故障", width = 15)
    @ApiModelProperty(value = "故障级别：1-普通故障 2-重大故障")
    @NotNull(message = "故障级别不能为空")
    private Integer faultLevel;

    /**故障位置*/
    @Excel(name = "故障位置", width = 15)
    @ApiModelProperty(value = "故障位置")
    private String location;

    /**故障详细位置*/
    @Excel(name = "故障详细位置", width = 15)
    @ApiModelProperty(value = "故障详细位置")
    private String detailLocation;

    /**影响范围*/
    @Excel(name = "影响范围", width = 15)
    @ApiModelProperty(value = "影响范围")
    private String scope;

    /**发生时间*/
    @Excel(name = "发生时间", width = 15)
    @ApiModelProperty(value = "发生时间")
    @NotNull(message = "故障发生时间不能为空")
    private Date occurrenceTime;

    /**报修方式*/
    @Excel(name = "报修方式", width = 15)
    @ApiModelProperty(value = "报修方式")
    @NotBlank(message = "报修方式不能为空")
    private String repairWay;

    /**报修编号*/
    @Excel(name = "报修编号", width = 15)
    @ApiModelProperty(value = "报修编号")
    private String repairCode;

    /**系统编号*/
    @Excel(name = "系统编号", width = 15)
    @ApiModelProperty(value = "系统编号")
    @NotBlank(message = "系统编号不能为空")
    private String systemCode;

    /**删除状态：0.未删除 1已删除*/
    @Excel(name = "删除状态：0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态：0.未删除 1已删除")
    @TableLogic
    private Integer delFlag;

    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**修改人*/
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    /**创建时间，CURRENT_TIMESTAMP*/
    @Excel(name = "创建时间，CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间，CURRENT_TIMESTAMP")
    private Date createTime;

    /**修改时间，根据当前时间戳更新*/
    @Excel(name = "修改时间，根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间，根据当前时间戳更新")
    private Date updateTime;


    @Excel(name = "任务表id", width = 15)
    @ApiModelProperty(value = "任务表id")
    private Long taskId;

    @Excel(name = "常见故障id", width = 15)
    @ApiModelProperty(value = "常见故障id")
    private Long commonFaultId;

    @Excel(name = "巡检任务项id", width = 15)
    @ApiModelProperty(value = "巡检任务项id")
    private Long poolContentId;

    @Excel(name = "检修任务id", width = 15)
    @ApiModelProperty(value = "检修任务id")
    private Long repairTaskId;

    /**附件列表*/
    public List<String> urlList;
}
