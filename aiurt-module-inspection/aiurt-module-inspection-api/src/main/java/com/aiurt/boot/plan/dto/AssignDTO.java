package com.aiurt.boot.plan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/2416:33
 */
@Data
public class AssignDTO {
    @NotNull
    @ApiModelProperty(value = "检修池ids", required = true)
    private List<String> ids;
    @NotNull(message = "请选择指派人员")
    @ApiModelProperty(value = "指派人员ids", required = true)
    private List<String> userIds;
    @NotNull(message = "请填写计划开始检修时间")
    @ApiModelProperty(value = "计划开始检修时间", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;
    @NotNull(message = "请填写计划结束检修时间")
    @ApiModelProperty(value = "计划结束检修时间", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;
    @NotBlank(message = "请选择作业类型")
    @ApiModelProperty(value = "作业类型", required = true)
    private String workType;
    @ApiModelProperty(value = "计划令编码")
    private String planOrderCode;
    @ApiModelProperty(value = "计划令图片")
    private String planOrderCodeUrl;
    @ApiModelProperty(value = "是否是手工下发任务，0否1是")
    private java.lang.Integer isManual;
}
