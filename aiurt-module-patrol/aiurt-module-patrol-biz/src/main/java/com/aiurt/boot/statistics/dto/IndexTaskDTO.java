package com.aiurt.boot.statistics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class IndexTaskDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页")
    @Value(value = "1")
    @NotNull(message = "当前页值为空")
    private Integer pageNo;
    /**
     * 页面大小
     */
    @ApiModelProperty(value = "页面大小")
    @NotNull(message = "页面大小为空")
    @Value(value = "10")
    private Integer pageSize;
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
    /**
     * 巡检结果：0异常、1正常
     */
    @ApiModelProperty(value = "巡检结果：0异常、1正常")
//    @NotNull(message = "任务的异常状态不能为空")
    private Integer state;
    /**
     * 站点编号
     */
    @ApiModelProperty(value = "站点编号")
    private String stationCode;
    /**
     * 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成
     */
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成")
    private Integer[] status;
    /**
     * 漏巡状态,0未漏检，1已漏检
     */
    @ApiModelProperty(value = "漏巡状态:0未漏检，1已漏检,查询漏检总数列表下的任务列表该字段必传")
    private Integer omitStatus;
//    /**
//     * 任务编号
//     */
//    @ApiModelProperty(value = "任务编号")
//    private List<String> taskCode;
}
