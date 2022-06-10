package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DepartDataVo {
    @ApiModelProperty(value = "班组id")
    private String id;
    @ApiModelProperty(value = "班组名称")
    private String name;
    @ApiModelProperty(value = "人数")
    private Integer num;
    @ApiModelProperty(value = "班长")
    private String banzhang;
    @ApiModelProperty(value = "成员")
    private List<UserScheduleVo> userList;
}
