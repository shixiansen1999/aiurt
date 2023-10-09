package com.aiurt.modules.multideal.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
@ApiModel("加减签请请求参数对象")
public class AddReduceMultiInstanceDTO implements Serializable {

    @ApiModelProperty(value = "加/减签人员", required = true)
    private List<String> userNameList;


    @ApiModelProperty(value = "原因", required = true)
    private String reason;

    @ApiModelProperty(value = "任务id")
    private String taskId;
}
