package com.aiurt.modules.faultalarm.dto.resp;

import com.aiurt.common.aspect.annotation.Dict;
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
    @Dict(dicCode = "alm_deal_state")
    private Integer state;

    @ApiModelProperty(value = "告警级别编号")
    @Dict(dicCode = "alm_level")
    private Integer level;

    @ApiModelProperty(value = "告警描述")
    private String almText;

    @ApiModelProperty(value = "设备ID")
    @Dict(dictTable = "device", dicCode = "id", dicText = "name")
    private String deviceId;

    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    @ApiModelProperty(value = "最后告警时间(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastAlmTime;

    @ApiModelProperty(value = "处理说明")
    private String dealRemark;

    @ApiModelProperty(value = "处理时间(yyyy-MM-dd)")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dealDateTime;

    @ApiModelProperty(value = "处理人ID")
    @Dict(dictTable = "sys_user", dicCode = "id", dicText = "realname")
    private String dealUserId;

    @ApiModelProperty(value = "站点编码")
    @Dict(dictTable = "cs_station", dicCode = "station_code", dicText = "station_name")
    private String stationCode;

    @ApiModelProperty(value = "设备类型编码")
    @Dict(dictTable = "device_type", dicCode = "code", dicText = "name")
    private String deviceTypeCode;

    @ApiModelProperty(value = "告警重复次数")
    private Integer almNum;

    @ApiModelProperty(value = "专业编码")
    @Dict(dictTable = "cs_major", dicCode = "major_code", dicText = "major_name")
    private String majorCode;

    @ApiModelProperty(value = "子系统")
    @Dict(dictTable = "cs_subsystem", dicCode = "system_code", dicText = "system_name")
    private String subSystemCode;

    @ApiModelProperty(value = "工单编号")
    private String faultCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
