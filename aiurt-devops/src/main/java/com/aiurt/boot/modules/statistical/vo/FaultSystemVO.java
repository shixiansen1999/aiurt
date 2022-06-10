package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author renanfeng
 * @version 1.0
 * @date 2022/01/25 13:30
 */
@Data
public class FaultSystemVO {

    @ApiModelProperty(value = "系统")
    private String systemName;
    @ApiModelProperty(value = "站点")
    private String station;
    @ApiModelProperty(value = "故障时间")
    private Date occurrenceTime;
    @ApiModelProperty(value = "指派人")
    private String appointUser;
    @ApiModelProperty(value = "维修人")
    private String maintenanceUser;
    @ApiModelProperty(value = "状态 0:待派单 1:未修复 2:已修复")
    private int status;
    @ApiModelProperty(value = "故障编号")
    private String code;

    public String getMaintenanceUser() {
        if(StringUtils.isNotEmpty(this.appointUser)){
            if(StringUtils.isNotEmpty(this.maintenanceUser)&&!this.maintenanceUser.contains(this.appointUser)){
                maintenanceUser=maintenanceUser+","+appointUser;
            }else{
                maintenanceUser=appointUser;
            }
        }
        return maintenanceUser;
    }
}
