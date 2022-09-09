package com.aiurt.boot.statistics.model;

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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PatrolCondition implements Serializable {
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
     * 站点code
     */
    @ApiModelProperty(value = "站点code")
    private String stationCode;
    /**
     * 线路code
     */
    @ApiModelProperty(value = "线路code")
    private String lineCode;
    /**
     * 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成
     */
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成")
    private Integer[] status;
    /**
     * 漏巡状态,0未漏检，1已漏检
     */
    @ApiModelProperty(value = "漏巡状态:0未漏检，1已漏检")
    private Integer omitStatus;
    /**
     * 完成状态：0未完成，1已完成
     */
    @ApiModelProperty(value = "完成状态：0未完成，1已完成")
    private Integer finishStatus;
//    /**
//     * 手工下发标志
//     */
//    @ApiModelProperty(value = "手工下发标志,字典值3 为手工下发")
//    private Integer source = 3;

}
