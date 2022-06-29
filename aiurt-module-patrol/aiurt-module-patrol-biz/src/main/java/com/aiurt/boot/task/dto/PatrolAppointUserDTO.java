package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "巡检指派用户信息对象", description = "巡检指派用户信息对象")
@NoArgsConstructor
@AllArgsConstructor
public class PatrolAppointUserDTO {
    /**
     * 巡检用户ID
     */
    @ApiModelProperty(value = "巡检用户ID")
    private java.lang.String userId;
    /**
     * 巡检用户名
     */
    @ApiModelProperty(value = "巡检用户名")
    private java.lang.String userName;
}
