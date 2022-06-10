package com.aiurt.boot.modules.fault.dto;

import com.swsc.copsms.modules.fault.entity.Fault;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;
import java.util.List;
@Data
public class FaultDTO{

    /**主键id,自动递增*/
    private Long id;
    /**故障编号，示例：G101.2109.001*/
    private String code;
    /**线路编号*/
    private String lineCode;
    /**站点编号*/
    private String stationCode;
    /**故障设备编号集合*/
    private String devicesIds;
    /**故障现象*/
    private String faultPhenomenon;
    /**故障类型*/
    private Integer faultType;
    /**状态：0-新报修 1-维修中 2-维修完成*/
    private Integer status;
    /**指派状态：0-未指派 1-指派 2-重新指派*/
    private Integer assignStatus;
    /**故障级别：1-普通故障 2-重大故障*/
    private Integer faultLevel;
    /**故障位置*/
    private String location;
    /**影响范围*/
    private String scope;
    /**发生时间*/
    private Date occurrenceTime;
    /**报修方式*/
    private String repairWay;
    /**系统编号*/
    private String systemCode;
    /**删除状态：0.未删除 1已删除*/
    private Integer delFlag;
    /**创建人*/
    private String createBy;
    /**修改人*/
    private String updateBy;
    /**创建时间，CURRENT_TIMESTAMP*/
    private Date createTime;
    /**修改时间，根据当前时间戳更新*/
    private Date updateTime;
    /**附件列表*/
    public List<String> urlList;
}
