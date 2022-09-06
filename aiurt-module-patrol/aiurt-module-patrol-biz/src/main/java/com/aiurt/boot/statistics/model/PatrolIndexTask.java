package com.aiurt.boot.statistics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

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
     * 组织机构编号
     */
    @ApiModelProperty(value = "组织机构编号")
    private String orgCode;
    /**
     * 组织机构名称
     */
    @ApiModelProperty(value = "组织机构名称")
    private String orgName;
    /**
     * 巡视人员ID
     */
    @ApiModelProperty(value = "巡视人员ID")
    private String userId;
    /**
     * 巡视人员名称
     */
    @ApiModelProperty(value = "巡视人员名称")
    private String username;
    /**
     * 提交时间
     */
    @ApiModelProperty(value = "提交时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String submitTime;
    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    private Integer status;
}
