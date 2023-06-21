package com.aiurt.modules.faultalarm.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author:wgp
 * @create: 2023-06-06 09:55
 * @Description:
 */
@Data
public class OnAlm {
    @ApiModelProperty(value = "记录ID")
    private Long guid;

    @ApiModelProperty(value = "设备ID")
    private String equipmentId;

    @ApiModelProperty(value = "日期时间")
    private Date dateTime;

    @ApiModelProperty(value = "级别")
    private Integer level;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "CPU")
    private String cpu;

    @ApiModelProperty(value = "CPU位置")
    private String cpuLoc;

    @ApiModelProperty(value = "告警元素")
    private String almElement;

    @ApiModelProperty(value = "元素位置")
    private String locInElement;

    @ApiModelProperty(value = "告警编码")
    private Integer almIndex;

    @ApiModelProperty(value = "告警组")
    private String almGrp;

    @ApiModelProperty(value = "告警编号")
    private String almNum;

    @ApiModelProperty(value = "告警文本")
    private String almText;

    @ApiModelProperty(value = "是否受理")
    private Boolean ack;

    @ApiModelProperty(value = "受理时间")
    private Date ackTime;

    @ApiModelProperty(value = "受理用户")
    private String ackUser;

    @ApiModelProperty(value = "受理信息")
    private String ackMsg;

    @ApiModelProperty(value = "保存时间")
    private Date saveTime;

    @ApiModelProperty(value = "equipment_id+alm_index唯一确定一个设备")
    private String equipmentGuid;
}