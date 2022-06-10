package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserScheduleVo {
    @ApiModelProperty(value = "id")
    private String userId;
    @ApiModelProperty(value = "姓名")
    private String userName;
    @ApiModelProperty(value = "是否值班0否1是")
    private Integer isSchedule;

}
