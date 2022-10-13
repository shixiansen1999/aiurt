package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("流程任务历史数据")
public class HistoricTaskInfo implements Serializable {

    @ApiModelProperty("活动id")
    private String id;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("办理人")
    private String assigne;

    @ApiModelProperty(value = "昵称")
    private String assignName;

    @ApiModelProperty("处理结果")
    private String state;

    @ApiModelProperty("耗时时间")
    private String costTime;

}
