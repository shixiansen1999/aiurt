package com.aiurt.boot.task.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@ApiModel(value = "巡检指派信息", description = "巡检指派信息")
@NoArgsConstructor
@AllArgsConstructor
public class PatrolAppointInfoDTO {
    /**
     * 任务指派信息K-V
     */
    @ApiModelProperty(value = "任务指派信息K-V,key表示任务编号，value表示用户信息列表", required = true)
    private Map<String, List<PatrolAppointUserDTO>> map;
    /**
     * 计划令编码
     */
    @ApiModelProperty(value = "计划令编码")
    private java.lang.String planOrderCode;
    /**
     * 计划令图片
     */
    @ApiModelProperty(value = "计划令图片")
    private java.lang.String planOrderCodeUrl;
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间,格式HH:mm")
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date startTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间,格式HH:mm")
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date endTime;
}
