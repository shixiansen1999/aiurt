package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRecordNodeInfoDTO implements Serializable {

    @ApiModelProperty(value = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "账号")
    private String userName;

    @ApiModelProperty(value = "用户部门，角色，岗位信息")
    private String userInfo;

    @ApiModelProperty(value = "审批时间")
    private Date endTime;

    @ApiModelProperty(value = "办理意见")
    private String reason;

}
