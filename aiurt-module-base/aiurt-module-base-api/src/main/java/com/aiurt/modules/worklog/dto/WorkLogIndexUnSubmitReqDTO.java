package com.aiurt.modules.worklog.dto;

import com.aiurt.modules.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 首页-工作日志-未提交日志的请求DTO
 * @author 华宜威
 * @date 2023-07-25 16:10:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkLogIndexUnSubmitReqDTO extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "查询开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @ApiModelProperty(value = "查询结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    @ApiModelProperty(value = "查询的班组id")
    private String orgId;
}
