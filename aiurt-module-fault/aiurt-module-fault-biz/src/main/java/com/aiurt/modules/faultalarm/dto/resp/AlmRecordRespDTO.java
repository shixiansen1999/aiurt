package com.aiurt.modules.faultalarm.dto.resp;

import com.aiurt.modules.basic.entity.DictEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author:wgp
 * @create: 2023-06-05 09:38
 * @Description: 告警记录响应DTO
 */
@Data
public class AlmRecordRespDTO extends DictEntity {

    @ApiModelProperty(value = "告警记录id")
    private String id;

    @ApiModelProperty(value = "告警发生时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date almTime;

    @ApiModelProperty(value = "处理状态编号")
    private Integer state;

    @ApiModelProperty(value = "告警级别编号")
    private Integer level;

    @ApiModelProperty(value = "告警描述")
    private String almText;

    @ApiModelProperty(value = "设备ID")
    private String deviceId;

    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    @ApiModelProperty(value = "最后告警时间(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastAlarmTime;

    @ApiModelProperty(value = "处理说明")
    private String dealRemark;

    @ApiModelProperty(value = "处理时间(yyyy-MM-dd)")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dealDateTime;

    @ApiModelProperty(value = "处理人ID")
    private String dealUserId;

    @ApiModelProperty(value = "如果取消告警，记录取消时间+30分钟到此字段")
    private Date timeAfter30Minutes;

    @ApiModelProperty(value = "站点编码")
    private String stationCode;

    @ApiModelProperty(value = "站点")
    private String stationName;

    @ApiModelProperty(value = "设备类型")
    private String deviceTypeName;

    @ApiModelProperty(value = "告警重复次数")
    private Integer almNum;

    @ApiModelProperty(value = "专业编码")
    private String majorCode;

    @ApiModelProperty(value = "专业名称")
    private String majorName;

    @ApiModelProperty(value = "子系统")
    private String subSystemCode;

    @ApiModelProperty(value = "子系统名称")
    private String subSystemName;

    @ApiModelProperty(value = "工单编号")
    private String faultCode;
}
