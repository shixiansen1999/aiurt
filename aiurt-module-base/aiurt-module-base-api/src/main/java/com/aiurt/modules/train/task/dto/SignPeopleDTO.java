package com.aiurt.modules.train.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.aiurt.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @projectName: jeecg-boot-parent
 * @package: org.jeecg.modules.train.task.dto
 * @className: SignPeopleDTO
 * @author: life-0
 * @date: 2022/5/13 16:25
 * @description: TODO
 * @version: 1.0
 */
@Data
public class SignPeopleDTO {
    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;
    /**用户名*/
    @Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private String userName;
    /**签到状态(1已签到.0未签到)*/
    @Excel(name = "签到状态(1已签到.0未签到)", width = 15)
    @ApiModelProperty(value = "签到状态(1已签到.0未签到)")
    @Dict(dicCode = "signState_type")
    private Integer signState;
    /**培训轮数*/
    @Excel(name = "buqian轮数", width = 15)
    @ApiModelProperty(value = "培训轮数")
    private Integer number;
    /**培训名称*/
    @Excel(name = "培训名称", width = 15)
    @ApiModelProperty(value = "培训名称")
    private String taskName;
    /**签到时间*/
    @Excel(name = "签到时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "签到时间")
    private Date signTime;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**签到方式*/
    @Excel(name = "签到方式 : 0为APP签到,1为后台签到", width = 15)
    @ApiModelProperty(value = "签到方式 : 0为APP签到,1为后台签到")
    private Integer stateSign;
    /**培训任务id*/
    @Excel(name = "培训任务id")
    @ApiModelProperty(value = "培训任务id")
    private String trainTaskId;
}
