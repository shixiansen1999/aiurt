package com.aiurt.boot.statistics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AbnormalDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 开始时间，格式yyyy-MM-dd
     */
    @ApiModelProperty(value = "开始时间，格式yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "开始时间不能为空")
    private Date startDate;
    /**
     * 结束时间，格式yyyy-MM-dd
     */
    @ApiModelProperty(value = "结束时间，格式yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "结束时间不能为空")
    private Date endDate;
    /**
     * 组织机构编号
     */
    @ApiModelProperty(value = "组织机构编号")
    private String orgCode;
    /**
     * 巡视用户名称
     */
    @ApiModelProperty(value = "巡视用户名称")
    private String username;
    @ApiModelProperty(value = "巡检结果：0异常、1正常")
//    @NotNull(message = "任务的异常状态不能为空")
    private Integer state;
    @ApiModelProperty(value = "站点编号")
    private String stationCode;
}
