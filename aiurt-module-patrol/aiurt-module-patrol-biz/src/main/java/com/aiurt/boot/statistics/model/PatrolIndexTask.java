package com.aiurt.boot.statistics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @author JB
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PatrolIndexTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 站点编号
     */
    @ApiModelProperty(value = "站点编号")
    private String stationCode;
    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    /**
     * 线路编号
     */
    @ApiModelProperty(value = "线路编号")
    private String lineCode;
    /**
     * 线路名称
     */
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    /**
     * 组织机构信息
     */
    @ApiModelProperty(value = "组织机构信息")
    private String orgInfo;
    /**
     * 巡视人员信息
     */
    @ApiModelProperty(value = "巡视人员信息")
    private String userInfo;
    /**
     * 提交时间
     */
    @ApiModelProperty(value = "提交时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String submitTime;
    /**
     * 任务状态,0未完成，1已完成
     */
    @ApiModelProperty(value = "任务状态,0未完成，1已完成")
    private Integer status;
    /**
     * 任务状态名称,0未完成，1已完成
     */
    @ApiModelProperty(value = "任务状态名称,0未完成，1已完成")
    private String statusName;
    /**
     * 任务编号
     */
    @ApiModelProperty(value = "任务编号")
    private String taskCode;

    @ApiModelProperty(value = "组织机构编码")
    private String orgCode;
}
