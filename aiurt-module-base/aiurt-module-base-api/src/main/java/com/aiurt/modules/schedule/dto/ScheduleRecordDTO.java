package com.aiurt.modules.schedule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/910:59
 */
@Data
public class ScheduleRecordDTO {

    @ApiModelProperty(value = "开始时间yyyy-MM-dd",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "时间不能为空")
    private java.util.Date startTime;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "值班类型")
    private Integer shift;
    @ApiModelProperty("第几页")
    private Integer pageNo =1;
    @ApiModelProperty("每页显示条数")
    private Integer pageSize = 10;
}
