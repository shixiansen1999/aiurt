package com.aiurt.modules.maplocation.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description: 报警记录DTO
 * @date 2021/5/79:18
 */
@Data
public class AlarmRecordDTO {
    @ApiModelProperty("上传人姓名")
    private String name;
    @ApiModelProperty("上传位置时间")
    private Date uploadLocationTime;
    @ApiModelProperty("报警开始时间")
    private Date alarmStartTime;
    @ApiModelProperty("报警结束时间")
    private Date alarmEndTime;
    @ApiModelProperty("状态")
    private String state;
}
