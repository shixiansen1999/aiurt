package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cgkj0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "任务重新生成对象信息", description = "任务重新生成对象信息")
public class PatrolRebuildDTO {
    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID", required = true)
    private String taskId;
    /**
     * 组织机构编号
     */
    @ApiModelProperty(value = "组织机构编号")
    private String[] deptCode;
    /**
     * 站所编号
     */
    @ApiModelProperty(value = "站所编号")
    private String[] stationCode;
}
