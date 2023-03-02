package com.aiurt.modules.usageconfig.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zwl
 */
@Data
@ApiModel("请求参数")
public class UsageConfigParamDTO {

    @ApiModelProperty(value = "父级pid")
    private String pid;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "基础数据0， 业务数据1", required = true)
    private Integer sign;

    @ApiModelProperty(required = true)
    private Integer pageNo=1;

    @ApiModelProperty(required = true)
    private Integer pageSize=10;

}
