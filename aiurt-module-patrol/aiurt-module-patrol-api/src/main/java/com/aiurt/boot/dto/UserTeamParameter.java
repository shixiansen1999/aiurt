package com.aiurt.boot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/10/9
 * @desc
 */
@Data
public class UserTeamParameter {
    @ApiModelProperty(value = "用户Id")
    private String userId;
    @ApiModelProperty(value = "班组List")
    private List<String> orgIdList;
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;
}
