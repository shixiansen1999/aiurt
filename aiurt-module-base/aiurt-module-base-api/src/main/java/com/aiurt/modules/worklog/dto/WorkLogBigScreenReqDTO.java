package com.aiurt.modules.worklog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 大屏-工作日志请求DTO
 * @author 华宜威
 * @date 2023-07-10 12:09:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkLogBigScreenReqDTO implements Serializable {

    @ApiModelProperty(value = "班组id")
    private String teamId;

    /**提交时间查询*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "提交时间查询")
    private Date submitTime;
}
