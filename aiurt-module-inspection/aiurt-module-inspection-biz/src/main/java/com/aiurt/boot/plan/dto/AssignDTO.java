package com.aiurt.boot.plan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
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
    @ApiModelProperty(value = "检修池ids",required = true)
    private List<String> ids;
    @NotNull
    @ApiModelProperty(value = "指派人员ids",required = true)
    private List<String> userIds;
    @NotNull
    @ApiModelProperty(value = "计划开始检修时间",required = true)
    private String startTime;
    @NotNull
    @ApiModelProperty(value = "计划结束检修时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String endTime;
    @NotNull
    @ApiModelProperty(value = "作业类型",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String workType;
    @ApiModelProperty(value = "计划令编码")
    private String planOrderCode;
    @ApiModelProperty(value = "计划令图片")
    private String planOrderCodeUrl;
}
